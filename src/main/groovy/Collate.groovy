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

assert (1..8).collate(3) == [[1, 2, 3], [4, 5, 6], [7, 8]]
assert (1..8).collate(3, false) == [[1, 2, 3], [4, 5, 6]]
assert (1..5).collate(3, 1, false) == [[1, 2, 3], [2, 3, 4], [3, 4, 5]]
assert (1..5).collate(3, 1) == [[1, 2, 3], [2, 3, 4], [3, 4, 5], [4, 5], [5]]
assert (1..8).collate(3, 2) == [[1, 2, 3], [3, 4, 5], [5, 6, 7], [7, 8]]
assert (1..8).collate(3, 2, false) == [[1, 2, 3], [3, 4, 5], [5, 6, 7]]
assert (1..8).collate(3, 4, false) == [[1, 2, 3], [5, 6, 7]]
assert (1..8).collate(3, 3) == [[1, 2, 3], [4, 5, 6], [7, 8]]
