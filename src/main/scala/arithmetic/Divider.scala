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

    val stepI       =   RegInit ((bitWidth-1).U(log2Ceil(bitWidth).W));

    val start :: calculate :: done :: Nil = Enum(3)
    val state = RegInit(start);

    switch (state) {
        
        is (start) {

            when (io.start) {
                divisor     :=  io.divisor
                dividend    :=  io.dividend
                state       :=  calculate
            }
        }

        is (calculate) {

            when (io.start) {

                when (divisor === 0.U) {
                    remainder := dividend
                    state := done
                }

                val intermediateRem =  (remainder << 1.U ) + io.dividend(stepI)  // R' = 2*R + A[i];

                /* 
                if (R' == B) {
                    Q[i] = 1;
                    R = 0;
                }
                else {
                    Q[i] = 0;
                    R = R';
                }
                */
                val remEqualToDivident = (intermediateRem === io.divisor);
                quotient(stepI) := Mux(remEqualToDivident , 1.U , 0.U)
                remainder := Mux(remEqualToDivident , 0.U , intermediateRem) 

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

            when (io.start) {
                state   :=  start
            }
            
        }

    }
}