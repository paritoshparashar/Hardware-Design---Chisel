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

    val remainder   =   RegInit(0.U(bitWidth.W))       //current remainder
    val quotient    =   RegInit(VecInit(Seq.fill(bitWidth)(0.U(1.W))))   //= {dividend[i:0], quotient[N−1:i+1]}, where dividend is the input dividend and quotient is the final output quotient, and i is the current cycle
    val divisor     =   RegInit(0.U(bitWidth.W))         //divisor
    val dividend     =   RegInit(0.U(bitWidth.W))         //divisor

    val stepI       =   RegInit ((bitWidth-1).U(log2Ceil(bitWidth).W)); // N cycles count
    stepI       :=   (bitWidth - 1).U
    // Default Outputs
    io.remainder    :=  0.U;
    io.quotient     :=  0.asUInt;
    io.done         :=  false.B;

    

    val start :: calculate :: done :: Nil = Enum(3)
    val state = RegInit(start);

    switch (state) {
        
        is (start) {

            when (io.start) {
                stepI       :=   (bitWidth - 1).U
                io.remainder    :=  0.U;
                io.quotient     :=  0.asUInt;
                io.done         :=  false.B;

                divisor     :=  io.divisor
                dividend    :=  io.dividend
                state       :=  calculate
            }
        }

        is (calculate) {

            when (stepI >= 0.U) {

                when (divisor === 0.U) {
                    remainder := dividend
                    state := done
                }

                val intermediateRem =  (remainder << 1.U ) + dividend(stepI)  // R' = 2*R + A[i];

                when (intermediateRem < divisor) {
                    quotient(stepI) := 0.U
                    remainder       := intermediateRem
                }
                .otherwise {
                    quotient(stepI) := 1.U
                    remainder       := intermediateRem - divisor
                }

                when (stepI === 0.U) {
                    state := done
                }
                .otherwise {
                    stepI := stepI - 1.U
                }

            }
            .otherwise {
                state   :=  done
            }  

        }

        is (done) {

                io.remainder    :=  remainder;
                io.quotient     :=  quotient.asUInt;
                io.done         :=  true.B;
                state := start
                
        }

    }
}