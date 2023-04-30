public  class Instruction {
//can add type of instruction 
    int value; // the int value that represents the 32 bits of the instruction after converting from binary to int
    int timeInStage;
    public Instruction(int value) {
        this.value = value;
        this.timeInStage = 0;
    }
    
    public int getopcode() { 
        return value >> 28;
    }
    public int getRegister(int index,Computer computer) { 
        return computer.registerFile[index];
    }
    public void setRegister(int index, int value,Computer computer) { 
        if(index != 0) // to prevent writing into the ZERO register
            computer.registerFile[index] = value;
    }

    public void execute_in_FETCH_stage(Computer computer) throws ComputerException{

        switch(getopcode()) {
            case 0: FETCH_ADD(computer); break;
            case 1: FETCH_SUB(computer); break;
            case 2: FETCH_MUL(computer); break;
            case 3: FETCH_MOVI(computer); break;
            case 4: FETCH_JEQ(computer); break;
            case 5: FETCH_AND(computer); break;
            case 6: FETCH_XORI(computer); break;
            case 7: FETCH_JMP(computer); break;
            case 8: FETCH_LSL(computer); break;
            case 9: FETCH_LSR(computer); break;
            case 10: FETCH_MOVR(computer); break;
            case 11: FETCH_MOVM(computer); break;
            default: throw new ComputerException("opcode for instruction " + value + " is not valid");
        }
    }
    public void execute_in_DECODE_stage(Computer computer)throws ComputerException{
        switch(getopcode()) {
            case 0: DECODE_ADD(computer); break;
            case 1: DECODE_SUB(computer); break;
            case 2: DECODE_MUL(computer); break;
            case 3: DECODE_MOVI(computer); break;
            case 4: DECODE_JEQ(computer); break;
            case 5: DECODE_AND(computer); break;
            case 6: DECODE_XORI(computer); break;
            case 7: DECODE_JMP(computer); break;
            case 8: DECODE_LSL(computer); break;
            case 9: DECODE_LSR(computer); break;
            case 10: DECODE_MOVR(computer); break;
            case 11: DECODE_MOVM(computer); break;
            default: throw new ComputerException("opcode for instruction " + value + " is not valid");
        }
    }
    public void execute_in_EXECUTE_stage(Computer computer)throws ComputerException{

    }
    public void execute_in_MEMORY_stage(Computer computer)throws ComputerException{
        switch(getopcode()) {
            case 0: MEMORY_ADD(computer); break;
            case 1: MEMORY_SUB(computer); break;
            case 2: MEMORY_MUL(computer); break;
            case 3: MEMORY_MOVI(computer); break;
            case 4: MEMORY_JEQ(computer); break;
            case 5: MEMORY_AND(computer); break;
            case 6: MEMORY_XORI(computer); break;
            case 7: MEMORY_JMP(computer); break;
            case 8: MEMORY_LSL(computer); break;
            case 9: MEMORY_LSR(computer); break;
            case 10: MEMORY_MOVR(computer); break;
            case 11: MEMORY_MOVM(computer); break;
            default: throw new ComputerException("opcode for instruction " + value + " is not valid");
        }
    }
    public void execute_in_WRITEBACK_stage(Computer computer)throws ComputerException{
        switch(getopcode()) {
            case 0: WRITEBACK_ADD(computer); break;
            case 1: WRITEBACK_SUB(computer); break;
            case 2: WRITEBACK_MUL(computer); break;
            case 3: WRITEBACK_MOVI(computer); break;
            case 4: WRITEBACK_JEQ(computer); break;
            case 5: WRITEBACK_AND(computer); break;
            case 6: WRITEBACK_XORI(computer); break;
            case 7: WRITEBACK_JMP(computer); break;
            case 8: WRITEBACK_LSL(computer); break;
            case 9: WRITEBACK_LSR(computer); break;
            case 10: WRITEBACK_MOVR(computer); break;
            case 11: WRITEBACK_MOVM(computer); break;
            default: throw new ComputerException("opcode for instruction " + value + " is not valid");
        }
    }


    // ADD 
    public void FETCH_ADD(Computer computer) {
        

    }
    public void DECODE_ADD(Computer computer) {
        

    }
    public void EXECUTE_ADD(Computer computer) {
        

    }
    public void MEMORY_ADD(Computer computer) {
        

    }
    public void WRITEBACK_ADD(Computer computer) {
        

    }

    // SUB
    public void FETCH_SUB(Computer computer) {
        

    }
    public void DECODE_SUB(Computer computer) {
        

    }
    public void EXECUTE_SUB(Computer computer) {
        

    }
    public void MEMORY_SUB(Computer computer) {
        

    }
    public void WRITEBACK_SUB(Computer computer) {
        

    }

    // MUL
    public void FETCH_MUL(Computer computer) {
        

    }
    public void DECODE_MUL(Computer computer) {
        

    }
    public void EXECUTE_MUL(Computer computer) {
        

    }
    public void MEMORY_MUL(Computer computer) {
        

    }
    public void WRITEBACK_MUL(Computer computer) {
        

    }

    // MOVI
    public void FETCH_MOVI(Computer computer) {
        

    }
    public void DECODE_MOVI(Computer computer) {
        

    }
    public void EXECUTE_MOVI(Computer computer) {
        

    }
    public void MEMORY_MOVI(Computer computer) {
        

    }
    public void WRITEBACK_MOVI(Computer computer) {
        

    }

    // JEQ
    public void FETCH_JEQ(Computer computer) {
        

    }
    public void DECODE_JEQ(Computer computer) {
        

    }
    public void EXECUTE_JEQ(Computer computer) {
        

    }
    public void MEMORY_JEQ(Computer computer) {
        

    }
    public void WRITEBACK_JEQ(Computer computer) {
        

    }

    // AND
    public void FETCH_AND(Computer computer) {
        

    }
    public void DECODE_AND(Computer computer) {
        

    }
    public void EXECUTE_AND(Computer computer) {
        

    }
    public void MEMORY_AND(Computer computer) {
        

    }
    public void WRITEBACK_AND(Computer computer) {
        

    }

    // XORI
    public void FETCH_XORI(Computer computer) {
        

    }
    public void DECODE_XORI(Computer computer) {
        

    }
    public void EXECUTE_XORI(Computer computer) {
        

    }
    public void MEMORY_XORI(Computer computer) {
        

    }
    public void WRITEBACK_XORI(Computer computer) {
        

    }

    // JMP
    public void FETCH_JMP(Computer computer) {
        

    }
    public void DECODE_JMP(Computer computer) {
        

    }
    public void EXECUTE_JMP(Computer computer) {
        

    }
    public void MEMORY_JMP(Computer computer) {
        

    }
    public void WRITEBACK_JMP(Computer computer) {
        

    }

    // LSL
    public void FETCH_LSL(Computer computer) {
        

    }
    public void DECODE_LSL(Computer computer) {
        

    }
    public void EXECUTE_LSL(Computer computer) {
        

    }
    public void MEMORY_LSL(Computer computer) {
        

    }
    public void WRITEBACK_LSL(Computer computer) {
        

    }

    // LSR
    public void FETCH_LSR(Computer computer) {
        

    }
    public void DECODE_LSR(Computer computer) {
        

    }
    public void EXECUTE_LSR(Computer computer) {
        

    }
    public void MEMORY_LSR(Computer computer) {
        

    }
    public void WRITEBACK_LSR(Computer computer) {
        

    }

    // MOVR
    public void FETCH_MOVR(Computer computer) {
        

    }
    public void DECODE_MOVR(Computer computer) {
        

    }
    public void EXECUTE_MOVR(Computer computer) {
        

    }
    public void MEMORY_MOVR(Computer computer) {
        

    }
    public void WRITEBACK_MOVR(Computer computer) {
        

    }

    // MOVM
    public void FETCH_MOVM(Computer computer) {
        

    }
    public void DECODE_MOVM(Computer computer) {
        

    }
    public void EXECUTE_MOVM(Computer computer) {
        

    }
    public void MEMORY_MOVM(Computer computer) {
        

    }
    public void WRITEBACK_MOVM(Computer computer) {
        

    }
    


    

    
    
}
