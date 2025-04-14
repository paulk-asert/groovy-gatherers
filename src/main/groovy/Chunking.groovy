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

var nums = 1..8
assert nums[0..2] == [1, 2, 3]
assert nums[3<..<6] == [5, 6]
assert nums[0..2,3..4,5] == [1, 2, 3, 4, 5, 6]
assert nums[0..2] + nums[3..-1] == 1..8
assert nums[0,2,4,6] == [1,3,5,7]
assert nums[1,3,5,7] == [2,4,6,8]
assert nums.take(3) == [1, 2, 3]
assert nums.drop(2).take(3) == [3, 4, 5]

assert nums.stream().limit(3).toList() == [1, 2, 3]
assert nums.stream().skip(2).limit(3).toList() == [3, 4, 5]
