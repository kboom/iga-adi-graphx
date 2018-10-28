package edu.agh.kboom

import org.apache.spark.graphx.VertexId

case class IgaTaskContext(
                           vid: Int,
                           ec: ExecutionContext,
                           mc: IgaContext
                         ) {

  override def toString: String = f"(T${ec.id}%02d:V$vid%07d)"

}

object IgaTaskContext {

  def create(vid: VertexId)(implicit program: VertexProgram): IgaTaskContext = IgaTaskContext(vid.toInt, ExecutionContext(), program.ctx)

}
