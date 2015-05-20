"""
Copyright 2015 Zalando SE

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.
"""

import environmental


class Configuration:
    port = environmental.Int('PORT', 8080)
    job_interval = environmental.Int('JOB_INTERVAL', 15)  # how many seconds to wait between job runs
    github_token = environmental.Str('GITHUB_TOKEN', '')
