// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Fetches comments from the servers and adds them to the DOM.
 */
function getServerComments() {
    fetch('/data').then(response => response.json()).then((myObject) => {
        const commentsListElement = document.getElementById('comments-container');
        commentsListElement.innerHTML = '';
        for(x in myObject) {
                commentsListElement.appendChild(
                createListElement(myObject[x].date+" - "+myObject[x].comment));
        }
    });
}

function getComments() {
    fetch('/loginstatus').then(response => response.json()).then((myObject) => {
        if (myObject == "true") {
            getServerComments();
        } else {
            console.log("Not signed in");
        }
    });
}

/** 
 * Creates an <li> element containing text. 
 */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  console.log(liElement);
  return liElement;
}
