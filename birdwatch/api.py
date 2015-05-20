__author__ = 'jloeffler'

from github3 import GitHub
from github3.models import GitHubError
from github3.repos.repo import Repository
from birdwatch.configuration import configuration
from birdwatch.collector import Project

def list_projects():

    # just for testing
    github = GitHub(token=configuration.github_token)
    zalando_repos = github.iter_user_repos("zalando")

    projects = {'projects': [{'name': repo.name} for repo in zalando_repos if repo.name == 'birdwatch']}

    print(projects)

    return projects

def list_contributors():

    # just for testing
    github = GitHub(token=configuration.github_token)
    user = github.user('mrandi')

    return user.to_json()
