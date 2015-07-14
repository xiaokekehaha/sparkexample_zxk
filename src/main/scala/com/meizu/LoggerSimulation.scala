package com.meizu

import java.io.{ PrintWriter, OutputStreamWriter, IOException, BufferedOutputStream }
import java.util.concurrent.{ TimeUnit, Executors }
import java.net.ServerSocket

/**
 *
 * moni one record
 */
object LoggerSimulation {

  
  def test()={
    
    
  }
  def main(args: Array[String]) {
    /* if (args.length != 2) {
      System.err.println("Usage: <port> <millisecond>")
      System.exit(1)
    }*/

    val listener = new ServerSocket("9999".toInt)
    
     val socket = listener.accept()
    val out = new PrintWriter(socket.getOutputStream(), true)
   

    val content = "test" //  println(content)
    out.write(content + '\n')
    out.flush()
    socket.close()

  }
}