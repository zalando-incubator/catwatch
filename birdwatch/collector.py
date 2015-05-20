from collections import namedtuple
import itertools

from github3 import GitHub
from github3.models import GitHubError
from github3.repos.repo import Repository
from github3.users import User
from birdwatch.configuration import configuration


# turn the github3 and requests loggers a notch down
import logging
logging.getLogger('github3').setLevel(logging.ERROR)
logging.getLogger('requests').setLevel(logging.ERROR)

class Contributor:

    def __init__(self, member: User):
        print('USER', member.name)
        self.name = member.name
        self.handle = member.login
        self.avatar = member.gravatar_id



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
            self.contributors = list(repository.iter_collaborators())
        except GitHubError:
            self.contributors = []

        # TODO Actions

    @property
    def is_zalando(self) -> bool:
        """
        Checks if project is owned by zalando
        """
        return str(self.__repository.owner) == 'zalando'

def job():
    github = GitHub(token=configuration.github_token)
    zalando_repos = itertools.chain.from_iterable(github.iter_user_repos(org) for org in configuration.organizations)
    projects = {project.name: project for project in (Project(repo) for repo in zalando_repos)}
    print('Projects', len(projects))
    organizations = (github.organization(org) for org in configuration.organizations)
    members = itertools.chain.from_iterable(org.iter_members() for org in organizations)
    contributors = (Contributor(member) for member in members)
    contributors_map = {contributor.handle: contributor for contributor in contributors}
    print('Contributors', len(contributors_map))