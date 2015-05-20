from collections import namedtuple

from github3 import GitHub
from github3.models import GitHubError
from github3.repos.repo import Repository
from birdwatch.configuration import configuration


class Project:

    def __init__(self, repository: Repository):
        self.__repository = repository
        self.name = repository.name
        self.url = repository.html_url
        self.description = repository.description

        # Stats
        self.forks = repository.fork_count
        self.stars = repository.stargazers
        self.contributors = list(repository.iter_collaborators())

        # TODO Actions

    @property
    def is_zalando(self) -> bool:
        """
        Checks if project is owned by zalando
        """
        return str(self.__repository.owner) == 'zalando'

def job():
    github = GitHub(token=configuration.github_token)
    zalando_repos = github.iter_user_repos("zalando")
    for repo in zalando_repos:
        try:
            project = Project(repo)
        except GitHubError:
            continue
        print(project.name, project.contributors)