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

package com.google.sps.servlets;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.Comment;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    private Gson gson = new Gson();
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

      PreparedQuery userComments = datastore.prepare(query);

      List<Comment> commentList = new ArrayList<>();
      for (Entity entity : userComments.asIterable()) {
          long id = entity.getKey().getId();
          String newComment = (String) entity.getProperty("comment");
          long timestamp = (long) entity.getProperty("timestamp");

          Comment commentObject = new Comment(id, newComment, timestamp);
          commentList.add(commentObject);
      }

      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(commentList));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String comment = getParameter(request, "comment-input", "");
      long timestamp = System.currentTimeMillis();
      if(isValid(comment)) {
          Entity commentEntity = new Entity("Comment");
          commentEntity.setProperty("comment", comment);
          commentEntity.setProperty("timestamp", timestamp);

          datastore.put(commentEntity);
      }
      
      response.sendRedirect("/index.html");
  }

  /**
   * returns the request parameter, or the default value if the parameter
   * was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  /**
   * Checks if the user's comment is not empty or the placeholder text
   */
  public boolean isValid(String comment) {
      return !comment.isEmpty() && !comment.isBlank() && !comment.equalsIgnoreCase("New Comment");
   }
}
