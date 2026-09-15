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
import java.util.ArrayList;
import java.util.List;

public class JEP394 {
    public static void main(String[] args) {
        List<?> items = List.of("Pi", "Cat", "Camel", 3.14, new ArrayList<>());
        for (Object item : items) {
            if (item instanceof String s && s.length() < 3) {
                System.out.println(s.toUpperCase());
            }
            if (item instanceof String s && s.length() > 3) {
                System.out.println(s.toLowerCase());
            }
            if (!(item instanceof String s)) {
                return;
            }
            System.out.println(s.equalsIgnoreCase("cAT"));
        }
    }
}
// PI Pi cat 3.14 []
