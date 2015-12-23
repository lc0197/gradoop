/*
 * This file is part of Gradoop.
 *
 * Gradoop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gradoop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gradoop. If not, see <http://www.gnu.org/licenses/>.
 */

package org.gradoop.model.impl.operators.summarization.tuples;

import org.apache.flink.api.java.tuple.Tuple2;
import org.gradoop.model.impl.id.GradoopId;

/**
 * Representation of a vertex id and its corresponding vertex group
 * representative.
 *
 * f0: vertex id
 * f1: group representative vertex id
 */
public class VertexWithRepresentative extends Tuple2<GradoopId, GradoopId> {

  public void setVertexId(GradoopId vertexId) {
    f0 = vertexId;
  }

  public GradoopId getGroupRepresentative() {
    return f1;
  }

  public void setGroupRepresentative(GradoopId groupRepresentative) {
    f1 = groupRepresentative;
  }
}
