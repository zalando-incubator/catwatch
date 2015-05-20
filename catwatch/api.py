__author__ = 'jloeffler'

from github3 import GitHub
from github3.models import GitHubError
from github3.repos.repo import Repository
from catwatch.configuration import configuration
from catwatch.collector import Project

def list_projects():

    # just for testing
    github = GitHub(token=configuration.github_token)
    zalando_repos = github.iter_user_repos("zalando")

    projects = {'projects': [{'name': repo.name} for repo in zalando_repos if repo.name == 'catwatch']}

    print(projects)

    return projects

def list_contributors():

    # just for testing
    github = GitHub(token=configuration.github_token)
    user = github.user('mrandi')

    return {
            'name': user.name, 
            'avatar_url': user.avatar_url,
            'username': user.login,
            'stats': {
                'forks': 'TODO',
                'stars': 'TODO',
                'contributions': 'TODO',
                'overall': {
                    'commits': 'TODO',
                    'pull_requests': 'TODO',
                    'comments': 'TODO',
                    'approvals': 'TODO'
                },
                'last_month': {
                    'commits': 'TODO',
                    'pull_requests': 'TODO',
                    'comments': 'TODO',
                    'approvals': 'TODO'
                }
            }
        }
