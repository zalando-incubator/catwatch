#!/usr/bin/env python3

"""
Copyright 2015 Zalando SE

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.
"""

import logging

import apscheduler.schedulers.background as scheduler_background
import apscheduler.triggers.interval as scheduler_interval
import connexion

from catwatch.configuration import configuration
import catwatch.collector as collector

logger = logging.getLogger('catwatch')
logging.basicConfig(level=logging.DEBUG, style='{', format="{asctime} | {levelname: <7} | {name: <20.20} | {message}")


def setup_scheduler():
    # configure scheduler
    scheduler = scheduler_background.BackgroundScheduler()
    interval = scheduler_interval.IntervalTrigger(seconds=configuration.job_interval)
    # NEEDS to be removed: intended behavior: start job after bootup and rerun it after an hour
    collector.job()
    scheduler.add_job(collector.job, interval, max_instances=10)
    return scheduler


def setup_webapp(config):
    app = connexion.App(__name__, config.port, specification_dir='swagger/')
    app.add_api('catwatch-api.yaml')
    return app


def main():

    logger.info('Starting Scheduler')
    scheduler = setup_scheduler()
    scheduler.start()
    logger.info('Scheduler running')

    logger.info('Starting web app')
    app = setup_webapp(configuration)
    app.run()


if __name__ == '__main__':
    main()
