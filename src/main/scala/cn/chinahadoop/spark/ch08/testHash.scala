package api.examples

import scala.collection.mutable.HashMap



class testHash {

}
object testHash extends App{
 
  val m =HashMap("Tom"->3,"Disk"->4,"Harry"->5)
  val m2 =HashMap("Tom"->4,"Dik"->5,"Harry"->8)

  val data= comb(m,m2)
  for((k,v)<-data){
    
   // println(k+"="+v)
  }
  
  val rang=Range(1,10,4)
  rang.foreach(println)
  def merge(dfs: HashMap[String, Int], tfs: HashMap[String, Int])
      : HashMap[String, Int] = {
      tfs.keySet.foreach { term =>
        dfs += term -> (dfs.getOrElse(term, 0) + 1)
      }
      dfs
    }
    def comb(dfs1: HashMap[String, Int], dfs2: HashMap[String, Int])
      : HashMap[String, Int] = {
      for ((term, count) <- dfs2) {
        dfs1 += term -> (dfs1.getOrElse(term, 0) + count)
      }
      dfs1
    }
  

   
   // docTermFreqs.aggregate(zero)(merge, comb)
}