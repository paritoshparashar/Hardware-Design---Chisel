package RISCV.implementation.RV32M

import chisel3._
import chisel3.util._

import RISCV.interfaces.generic.AbstractExecutionUnit
import RISCV.model._
import cats.CoflatMap.ops


class MultiplicationUnit extends AbstractExecutionUnit {

    io.misa := "b01__0000__0_00000_00000_00100_00000_00000".U
    
    val opcode = RISCV_OP(io.instr(6, 0))
    val funct7 = RISCV_FUNCT7(io.instr(31, 25))

    when (opcode === RISCV_OP.OP) {
        when (funct7 === RISCV_FUNCT7.MULDIV) {
            io.valid := true.B
        }
    }.otherwise(
        io.valid := false.B)

    io.stall := STALL_REASON.NO_STALL

    io_data <> DontCare
    io_reg <> DontCare
    io_pc <> DontCare
    io_reset <> DontCare
    
    //TODO: Your solution to Problem 2.4 should go here


}
