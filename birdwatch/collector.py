from github3 import GitHub
from birdwatch.configuration import configuration


def job():
    github = GitHub(token=configuration.github_token)
    zalando_repos = github.iter_user_repos("zalando")
    for r in zalando_repos:
        print(r)