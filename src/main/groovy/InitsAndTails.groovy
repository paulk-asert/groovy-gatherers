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

assert (1..6).inits() == [[1, 2, 3, 4, 5, 6],
                          [1, 2, 3, 4, 5],
                          [1, 2, 3, 4],
                          [1, 2, 3],
                          [1, 2],
                          [1],
                          []]

assert (1..6).tails() == [[1, 2, 3, 4, 5, 6],
                             [2, 3, 4, 5, 6],
                                [3, 4, 5, 6],
                                   [4, 5, 6],
                                      [5, 6],
                                         [6],
                                          []]

var words = 'the quick brown fox jumped over the lazy dog'.split().toList()
var search = 'brown fox'.split().toList()

assert words.tails().any{ subseq -> subseq.inits().contains(search) }
assert Collections.indexOfSubList(words, search) != -1
