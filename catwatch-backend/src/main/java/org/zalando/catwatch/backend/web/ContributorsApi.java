package org.zalando.catwatch.backend.web;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Sets.SetView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.ContributorKey;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.util.Constants;

import javax.persistence.EmbeddedId;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Sets.intersection;
import static java.time.Instant.now;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;
import static java.util.Date.from;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.zalando.catwatch.backend.util.Constants.CONFIG_ORGANIZATION_LIST;
import static org.zalando.catwatch.backend.web.config.DateUtil.iso8601;

@Controller
@RequestMapping(value = Constants.API_RESOURCE_CONTRIBUTORS, produces = {APPLICATION_JSON_VALUE})
@Api(value = Constants.API_RESOURCE_CONTRIBUTORS, description = "the contributors API")
public class ContributorsApi {

    private final static int LIMIT_DEFAULT = 5;

    private final static List<String> SORT_BY_LIST = unmodifiableList(
            asList("organizationalCommitsCount", "organizationalProjectsCount", "personalCommitsCount",
                    "personalProjectsCount", "organizationName", "name"));

    @EmbeddedId
    private ContributorKey key;

    private final ContributorRepository repository;
    private final Environment env;

    @Autowired
    public ContributorsApi(ContributorRepository repository, Environment env) {
        this.repository = repository;
        this.env = env;
    }

    @ApiOperation(value = "Contributor", notes = "The Contributors endpoint returns all information like name, url, commits count, \nprojects count of all the Contributors for the selected filter. \n", response = Contributor.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "An array of Contributors of selected Github organization"),
            @ApiResponse(code = 0, message = "Unexpected error")})
    @RequestMapping(value = "",

            method = RequestMethod.GET)
    public
    @ResponseBody
    List<Contributor> contributorsGet(
            @ApiParam(value = "List of github.com organizations to scan(comma seperated)", required = true) //
            @RequestParam(value = Constants.API_REQUEST_PARAM_ORGANIZATIONS, required = true) //
                    String organizations, //

            @ApiParam(value = "Number of items to retrieve. Default is 5.") //
            @RequestParam(value = Constants.API_REQUEST_PARAM_LIMIT, required = false) //
                    Integer limit, //

            @ApiParam(value = "Offset the list of returned results by this amount. Default is zero.") //
            @RequestParam(value = Constants.API_REQUEST_PARAM_OFFSET, required = false) //
                    Integer offset, //

            @ApiParam(value = "Date from which to start fetching records from database(default = current_date)") //
            @RequestParam(value = Constants.API_REQUEST_PARAM_STARTDATE, required = false) //
                    String startDate, //

            @ApiParam(value = "Date till which records will be fetched from database(default = current_date)") //
            @RequestParam(value = Constants.API_REQUEST_PARAM_ENDDATE, required = false) //
                    String endDate, //

            @ApiParam(value = "parameter by which result should be sorted. '-' means descending order (default is count of commit)")
            //
            @RequestParam(value = Constants.API_REQUEST_PARAM_SORTBY, required = false) //
                    String sortBy, //

            @ApiParam(value = "query paramater for search query (this will be contributor names prefix)") //
            @RequestParam(value = Constants.API_REQUEST_PARAM_Q, required = false) //
                    String q //

    ) {

        validate(organizations, offset, limit, sortBy, startDate, endDate);

        if (startDate != null && endDate != null && repository.findPreviousSnapShotDate(iso8601(endDate)) != null
                && repository.findPreviousSnapShotDate(iso8601(startDate)) != null) {

            return contributorsGet_timeSpan(organizations, limit, offset, startDate, endDate, sortBy, q);

        } else if (startDate == null && endDate == null //
                && repository.findPreviousSnapShotDate(from(now())) != null) {

            return contributorsGet_noTimeSpan(organizations, limit, offset, endDate, sortBy, q);

        } else {

            throw new UnsupportedOperationException(
                    "this parameter configuration is not implemented yet" + " .. start date, end date required atm");

        }
    }

    private List<Contributor> contributorsGet_noTimeSpan(String organizations, Integer limit, Integer offset,
                                                         String endDate, String sortBy, String q) {

        Date endDateDate = endDate != null ? iso8601(endDate) : new Date();
        Date endDateInDb = repository.findPreviousSnapShotDate(endDateDate);

        ArrayListMultimap<Long, Contributor> multiMap = ArrayListMultimap.create();

        orgs(organizations).values().stream().forEach(organizationId -> {

            List<Contributor> contributors = repository.findAllTimeTopContributors(organizationId, endDateInDb, q, null,
                    null);

            contributors.stream().forEach(c -> multiMap.put(c.getKey().getId(), c));
        });

        List<Contributor> sorted = multiMap.asMap().values().stream().map(list -> add(list)).collect(toList());
        Collections.sort(sorted, sortBy(sortBy));

        return sublist(limit, offset, sorted);
    }

    private List<Contributor> contributorsGet_timeSpan(String organizations, Integer limit, Integer offset,
                                                       String startDate, String endDate, String sortBy, String q) {

        Date startDateInDb = repository.findPreviousSnapShotDate(iso8601(startDate));
        Date endDateInDb = repository.findPreviousSnapShotDate(iso8601(endDate));

        checkNotNull(startDateInDb);
        checkNotNull(endDateInDb);

        ArrayListMultimap<Long, Contributor> multiMap = ArrayListMultimap.create();

        orgs(organizations).values().stream().forEach(organizationId -> {

            List<Contributor> startData = repository.findAllTimeTopContributors(organizationId, startDateInDb, q, null,
                    null);

            Map<Long, Contributor> startMap = startData.stream().collect(toMap(Contributor::getId, identity()));

            List<Contributor> endData = repository.findAllTimeTopContributors(organizationId, endDateInDb, q, null,
                    null);

            Map<Long, Contributor> endMap = endData.stream().collect(toMap(Contributor::getId, identity()));

            SetView<Long> contributorIds = intersection(startMap.keySet(), endMap.keySet());

            contributorIds.stream() //
                    .map(id -> diff(startMap.get(id), endMap.get(id))) //
                    .forEach(c -> multiMap.put(c.getKey().getId(), c));
        });

        List<Contributor> sorted = multiMap.asMap().values().stream().map(list -> add(list)).collect(toList());
        Collections.sort(sorted, sortBy(sortBy));

        return sublist(limit, offset, sorted);
    }

    //
    // util
    //

    private List<Contributor> sublist(Integer limit, Integer offset, List<Contributor> sorted) {
        int endIndex;
        if (offset(offset) + limit(limit) > sorted.size()) {
            endIndex = sorted.size();
        } else {
            endIndex = offset(offset) + limit(limit);
        }
        sorted = sorted.subList(offset(offset), endIndex);

        return sorted;
    }

    //
    // validate / process arguments
    //

    private void validate(String organizations, Integer offset, Integer limit, String sortBy, String startDate,
                          String endDate) {

        checkArgument(offset(offset) >= 0, "offset must be greater than zero but was " + offset);

        checkArgument(limit(limit) > 0, "limit must be greater than zero but was " + limit);

        checkArgument(!orgs(organizations).containsValue(null), "an organization name was not found: " + organizations);

        checkArgument(sortBy(sortBy) != null, "sortBy must be empty or have a valid value but was " + sortBy
                + ". Valid values are " + on(",").join(SORT_BY_LIST));

        checkArgument(endDate == null || repository.findPreviousSnapShotDate(iso8601(endDate)) != null,
                "endDate is set to " + endDate + "but there is no snapshot data before that date");

        checkArgument(startDate == null || repository.findPreviousSnapShotDate(iso8601(startDate)) != null,
                "startDate is set to " + startDate + "but there is no snapshot data before that date");

        checkArgument(startDate == null || endDate == null || iso8601(startDate).before(iso8601(endDate)),
                "startDate " + startDate + " must be before endDate" + endDate + " but was not");
    }

    private Map<String, Long> orgs(String organizations) {
        if (isNullOrEmpty(organizations)) {
            organizations = env.getProperty(CONFIG_ORGANIZATION_LIST);
        }
        return stream(organizations.trim().split("\\s*,\\s*"))
                .collect(toMap(identity(), orgName -> repository.findOrganizationId(orgName)));
    }

    private int offset(Integer offset) {
        return offset == null ? 0 : offset;
    }

    private int limit(Integer limit) {
        if (limit != null) {
            return limit;
        } else {
            return LIMIT_DEFAULT;
        }
    }

    private Comparator<Contributor> sortBy(String sortBy) {
        if (Strings.isNullOrEmpty(sortBy)) {
            return comparator(SORT_BY_LIST.get(0), true);
        } else {
            // (this should be re-written or/and unit tested)
            sortBy = sortBy.trim();
            boolean reverse = false;
            if (sortBy.startsWith("-")) {
                reverse = true;
                sortBy = sortBy.substring(1);
            }
            String cleanedSortBy = SORT_BY_LIST.stream().collect(toMap(String::toLowerCase, identity()))
                    .get(sortBy.toLowerCase());
            if (cleanedSortBy != null) {
                return comparator(cleanedSortBy, reverse);
            } else {
                return null;
            }
        }
    }

    //
    // process data
    //

    private Contributor add(Collection<Contributor> collection) {
        Contributor any = collection.iterator().next();

        Contributor c = new Contributor(any.getId(), any.getOrganizationId(), any.getSnapshotDate());
        c.setName(any.getName());
        c.setUrl(any.getUrl());
        c.setOrganizationalCommitsCount(0);
        c.setOrganizationalProjectsCount(0);
        c.setPersonalCommitsCount(0);
        c.setPersonalProjectsCount(0);

        for (Contributor cc : collection) {
            c.setOrganizationalCommitsCount(add(c.getOrganizationalCommitsCount(), cc.getOrganizationalCommitsCount()));
            c.setOrganizationalProjectsCount(
                    add(c.getOrganizationalProjectsCount(), cc.getOrganizationalProjectsCount()));
            c.setPersonalCommitsCount(add(c.getPersonalCommitsCount(), cc.getPersonalCommitsCount()));
            c.setPersonalProjectsCount(add(c.getPersonalProjectsCount(), cc.getPersonalProjectsCount()));
        }

        return c;
    }

    private Contributor diff(Contributor start, Contributor end) {
        Contributor c = new Contributor(end.getId(), end.getOrganizationId(), end.getSnapshotDate());
        c.setName(end.getName());
        c.setUrl(end.getUrl());

        c.setOrganizationalCommitsCount(
                subtract(end.getOrganizationalCommitsCount(), start.getOrganizationalCommitsCount()));
        c.setOrganizationalProjectsCount(
                subtract(end.getOrganizationalProjectsCount(), start.getOrganizationalProjectsCount()));
        c.setPersonalCommitsCount(subtract(end.getPersonalCommitsCount(), start.getPersonalCommitsCount()));
        c.setPersonalProjectsCount(subtract(end.getPersonalProjectsCount(), start.getPersonalProjectsCount()));

        return c;
    }

    private Integer subtract(Integer x, Integer y) {
        return (x != null && y != null) ? x - y : null;
    }

    private Integer add(Integer x, Integer y) {
        return (x != null && y != null) ? x + y : null;
    }

    @SuppressWarnings("unchecked")
    private Comparator<Contributor> comparator(String sortBy, boolean reverse) {
        ComparatorChain comparator = new ComparatorChain();
        comparator.addComparator(new BeanComparator<Contributor>(sortBy), reverse);
        comparator.addComparator(new BeanComparator<Contributor>("id"));
        return comparator;
    }
}
