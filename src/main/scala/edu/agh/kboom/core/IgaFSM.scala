package edu.agh.kboom.core

import edu.agh.kboom.core.tree.ProblemTree

trait IgaState {
  val gen: Int
}

sealed case class InitialState(gen: Int) extends IgaState

sealed case class InitializeState(gen: Int) extends IgaState

sealed case class MergeAndEliminateLeavesState(gen: Int) extends IgaState

sealed case class MergeAndEliminateBranchesState(gen: Int) extends IgaState

sealed case class MergeAndEliminateInterimState(gen: Int) extends IgaState

sealed case class MergeAndEliminateRootState(gen: Int) extends IgaState

sealed case class BackwardSubstituteInterimState(gen: Int) extends IgaState

sealed case class BackwardSubstituteBranchesState(gen: Int) extends IgaState

sealed case class BackwardSubstituteLeavesState(gen: Int) extends IgaState

sealed case class EndState(gen: Int) extends IgaState

object IgaFSM {

  def initialState(): IgaState = InitialState(0)

  def nextState(cs: IgaState)(implicit tree: ProblemTree): IgaState = cs match {
    case InitialState(g) => InitializeState(g + 1)
    case InitializeState(g) => MergeAndEliminateLeavesState(g + 1)
    case MergeAndEliminateLeavesState(g) => MergeAndEliminateBranchesState(g + 1)
    case MergeAndEliminateBranchesState(g) => MergeAndEliminateInterimState(g + 1)
    case MergeAndEliminateInterimState(g) => if (ProblemTree.leafHeight - g > 1) MergeAndEliminateInterimState(g + 1) else MergeAndEliminateRootState(g + 1)
    case MergeAndEliminateRootState(g) => BackwardSubstituteInterimState(g + 1)
    case BackwardSubstituteInterimState(g) => if ((g % ProblemTree.leafHeight) < ProblemTree.branchingHeight - 1) BackwardSubstituteInterimState(g + 1) else BackwardSubstituteBranchesState(g + 1)
    case BackwardSubstituteBranchesState(g) => BackwardSubstituteLeavesState(g + 1)
    case BackwardSubstituteLeavesState(g) => EndState(g + 1)
  }

}
