from collections import namedtuple
import itertools
import pprint

from github3 import GitHub
from github3.models import GitHubError
from github3.repos.repo import Repository
from github3.users import User
from birdwatch.configuration import configuration


# turn the github3 and requests loggers a notch down
import logging
logging.getLogger('github3').setLevel(logging.ERROR)
logging.getLogger('requests').setLevel(logging.ERROR)

class Stats:

    def __init__(self):
        self.contributors = 0
        self.projects = 0
        self.stars = 0
        self.forks = 0

    def __str__(self):
        return pprint.pformat(vars(self))

class Contributor:

    def __init__(self, member: User):
        print('USER', member.name)
        self.name = member.name
        self.handle = member.login
        self.avatar = member.gravatar_id
        self.actions = 0
        self.projects = 0

    @property
    def score(self) -> int:
        return self.actions + self.projects

class Project:

    def __init__(self, repository: Repository):
        self.__repository = repository
        self.name = repository.name
        self.url = repository.html_url
        self.description = repository.description

        # Stats
        self.forks = repository.fork_count
        self.stars = repository.stargazers
        try:
            self.contributor_list = list(repository.iter_collaborators())
        except GitHubError:
            self.contributor_list = []
         # TODO Actions

    @property
    def contributors(self):
        return len(self.contributor_list)

    @property
    def is_zalando(self) -> bool:
        """
        Checks if project is owned by zalando
        """
        return str(self.__repository.owner) == 'zalando'

    @property
    def score(self) -> int:
        return self.forks + self.stars + self.contributors

def job():
    github = GitHub(token=configuration.github_token)
    zalando_repos = itertools.chain.from_iterable(github.iter_user_repos(org) for org in configuration.organizations)
    projects = {project.name: project for project in (Project(repo) for repo in zalando_repos)}
    stats = Stats()
    stats.projects = len(projects)
    stats.stars = sum(project.stars for project in projects.values())
    stats.forks = sum(project.forks for project in projects.values())

    organizations = (github.organization(org) for org in configuration.organizations)
    members = itertools.chain.from_iterable(org.iter_members() for org in organizations)
    contributors = (Contributor(member) for member in members)
    contributors_map = {contributor.handle: contributor for contributor in contributors}
    stats.contributors = len(contributors_map)

    print(stats)
    print()
    print('TOP {} Projects'.format(configuration.item_limit))
    print('---------------------------')
    sorted_projects = sorted(projects.values(), key=lambda project: project.score)
    top_projects = itertools.islice(sorted_projects, 0, configuration.item_limit)
    for i, project in enumerate(top_projects):
        print("{n} - {project.name}: {project.stars} stars, {project.forks} forks, {project.contributors} contributors".format(n=i, project=project))

    print()
    print('TOP {} Contributors'.format(configuration.item_limit))
    print('---------------------------')
    sorted_contributors = sorted(contributors_map.values(), key=lambda contributor: contributor.score)
    top_contributors = itertools.islice(sorted_contributors, 0, configuration.item_limit)
    for i, contributor in enumerate(top_contributors):
        print("{n} - {contributor.name}: {contributor.projects} projects, {contributor.actions} actions".format(n=i, contributor=contributor))

    print()
