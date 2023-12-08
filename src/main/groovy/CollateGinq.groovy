/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

assert GQL {
    from n in 1..8
    limit 3
    select n
} == [1, 2, 3]

assert GQL {
    from n in 1..8
    limit 2, 3
    select n
} == [3, 4, 5]

assert GQL {
    from ns in (
        from n in 1..5
        select n, (lead(n) over(orderby n)), (lead(n, 2) over(orderby n))
    )
    select ns
}*.toList() == [[1, 2, 3], [2, 3, 4], [3, 4, 5]]

