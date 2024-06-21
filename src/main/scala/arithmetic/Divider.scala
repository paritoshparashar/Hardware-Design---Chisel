package arithmetic

import chisel3._
import chisel3.util._

class Divider(bitWidth: Int) extends Module {
    val io = IO(new Bundle {
        val start = Input(Bool())
        val done = Output(Bool())
        val dividend = Input(UInt(bitWidth.W))
        val divisor = Input(UInt(bitWidth.W))
        val quotient = Output(UInt(bitWidth.W))
        val remainder = Output(UInt(bitWidth.W))
    })

    val myRemainder   =   RegInit(0.U(bitWidth.W))       //current remainder
    val myQuotient    =   RegInit(VecInit(Seq.fill(bitWidth)(0.U(1.W))))   //= {dividend[i:0], quotient[N−1:i+1]}, where dividend is the input dividend and quotient is the final output quotient, and i is the current cycle
    val myDivisor     =   RegInit(0.U(bitWidth.W))         //divisor
    val myDividend     =   RegInit(0.U(bitWidth.W))         //dividend
    val myDone     =   RegInit(false.B)         //dividend

    val stepI       =   RegInit ((bitWidth-1).U(log2Ceil(bitWidth).W)); // N cycles count
    // Default Outputs
    io.remainder    :=  io.dividend;
    io.quotient     :=  0.U;
    io.done         :=  false.B;

    

    val startState :: calculateState :: doneState :: Nil = Enum (3)
    val state = RegInit(startState);

    switch (state) {
        
        is (startState) {

            when (io.start) {
                stepI          :=   (bitWidth - 1).U
                myRemainder    :=  0.U;

                for (i <- 0 until bitWidth) {
                    myQuotient(i) := 0.U
                }

                myDone        :=  false.B;

                myDivisor     :=  io.divisor
                myDividend    :=  io.dividend
            }
            .otherwise {
                state       :=  calculateState
            }
        }

        is (calculateState) {

            when (!myDone) {

                when (myDivisor === 0.U) {
                    for (i <- 0 until bitWidth) {
                    myQuotient(i) := (1.U << bitWidth) - 1.U
                }
                    myRemainder := myDividend
                    state := doneState
                }

                .otherwise{

                    val intermediateRem =  (myRemainder << 1.U ) + myDividend(stepI)  // R' = 2*R + A[i];

                    when (intermediateRem < myDivisor) {
                        myQuotient(stepI) := 0.U
                        myRemainder       := intermediateRem
                    }
                    .otherwise {
                        myQuotient(stepI) := 1.U
                        myRemainder       := intermediateRem - myDivisor
                    }

                    //printf(p"stepI: $stepI, myDividend: $myDividend, intermediateRem: $intermediateRem, myQuotient: ${myQuotient.asUInt}, remainder: $myRemainder\n")
                    
                    when (stepI === 0.U) {
                        myDone  := true.B
                    }
                    .otherwise {
                        stepI := stepI - 1.U
                    }
                }
            }
            .otherwise {

                myDone := true.B
                state   :=  doneState
            }  

        }

        is (doneState) {

            when (!io.start) {
                io.remainder    :=  myRemainder;
                io.quotient     :=  myQuotient.asUInt;
                io.done         :=  true.B;
            }
            .otherwise {
                
                stepI          :=   (bitWidth - 1).U
                myRemainder    :=  0.U;

                for (i <- 0 until bitWidth) {
                    myQuotient(i) := 0.U
                }

                myDone        :=  false.B;

                myDivisor     :=  io.divisor
                myDividend    :=  io.dividend

                state := startState
            }
                
        }

    }
}