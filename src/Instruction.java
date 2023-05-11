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
         computer.Instruction_in_Fetch_Stage=new Instruction(computer.memory[computer.PC]);
         computer.PC++;
       
    	/*switch(getopcode()) {
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
        }*/
    }
    public void execute_in_DECODE_stage(Computer computer)throws ComputerException{
        int r1,r2,r3,shamt,imm,add;
    	int opcode=computer.Instruction_in_Decode_Stage.value>>28;
    	computer.execute[0]= opcode;
    	switch(opcode) {
            case 0: case 1: case 2: case 5:case 8:case 9:
            r1= ((value<<4)>>27);
            r2=(value<<9)>>27;
            r3=(value<<14)>>27;
            shamt=(value<<19)>>19;
            
            computer.execute[1]= r1;
            computer.execute[2]=r2 ;
            computer.execute[3]= r3;
            computer.execute[4]=shamt;
            break;
            
            case 3: case 4: case 6: case 10 : case 11:
            	r1= ((value<<4)>>27);
            	r2=(value<<9)>>27;
            	imm=(value<<14)>>>14;
            	 
                 computer.execute[1]= r1;
                 computer.execute[2]=r2 ;
                 computer.execute[3]= imm;
                 break;
                 
            
            case 7: 
            	add=(value<<4)>>>4;
            	
                computer.execute[1]= add;
                break;
            
           
            default: throw new ComputerException("opcode for instruction " + value + " is not valid");
        }
    }
    public void execute_in_EXECUTE_stage(Computer computer)throws ComputerException{
    	int res;
    	computer.memorystage[0]=computer.execute[0];
    	switch(computer.execute[0]) {
        
    	case 0: 
        res=getRegister(computer.execute[2], computer) + getRegister(computer.execute[3], computer);
        computer.memorystage[1]=computer.execute[1];
        computer.memorystage[2]=res;
        break;
        
        case 1: 
        res=getRegister(computer.execute[2], computer) -getRegister(computer.execute[3], computer);
        computer.memorystage[1]=res;
        break;
        
        case 2: 
        res=getRegister(computer.execute[2], computer) * getRegister(computer.execute[3], computer);
        computer.memorystage[1]=computer.execute[1];
        computer.memorystage[2]=res;
        break;
        
        case 3: 
        res=computer.execute[3];
        computer.memorystage[1]=computer.execute[1];
        computer.memorystage[2]=res;

        break;
        
        case 4:
        if(getRegister(computer.execute[1], computer)==getRegister(computer.execute[2], computer)) {
        	computer.PC=computer.PC+1+computer.execute[3];
        }
        	
        	 break;
        case 5:
        res=getRegister(computer.execute[2], computer) & getRegister(computer.execute[3], computer);
        computer.memorystage[1]=computer.execute[1];
        computer.memorystage[2]=res;
        break;
        
        case 6: res=getRegister(computer.execute[2], computer)^computer.execute[3];
        computer.memorystage[1]=computer.execute[1];
        computer.memorystage[2]=res;
        break;
        
        case 7: 
        computer.PC=(computer.PC>>28)<<28+computer.execute[1];	
         break;
        
        case 8: 
        	res=getRegister(computer.execute[2], computer)<<computer.execute[4];
        	computer.memorystage[1]=computer.execute[1];
            computer.memorystage[2]=res;
        
        case 9: 
        	res=getRegister(computer.execute[2], computer)>>>computer.execute[4];
        	computer.memorystage[1]=computer.execute[1];
            computer.memorystage[2]=res;; break;
        
        case 10: 
        	res=getRegister(computer.execute[2], computer)+computer.execute[3];
        	computer.memorystage[1]=computer.execute[1];
            computer.memorystage[2]=res;
        	break;
        
        case 11: 
        	res=getRegister(computer.execute[2], computer)+computer.execute[3];
        	computer.memorystage[1]=getRegister(computer.execute[1], computer);
            computer.memorystage[2]=res;
        	break;
        default: throw new ComputerException("opcode for instruction " + value + " is not valid");
    }
    }
   
    public void execute_in_MEMORY_stage(Computer computer)throws ComputerException{
       computer.wb[0]=computer.memorystage[0];
    	switch(computer.wb[0]) {
            case 0:   case 1:   case 2:   case 3:    case 4:  case 5: case 6:case 7: case 8:  case 9: 
            	computer.wb[1]=computer.memorystage[1];
            	computer.wb[2]=computer.memorystage[2];
            	break;
            case 10:
            	computer.wb[1]=computer.memorystage[1];
            	computer.wb[2]=computer.memory[computer.memorystage[2]];
            	
            	
            	break;
            case 11:
            	computer.memory[computer.memorystage[2]]=computer.memorystage[1];
            	
            	break;
            default: throw new ComputerException("opcode for instruction " + value + " is not valid");
        }
    }
    public void execute_in_WRITEBACK_stage(Computer computer)throws ComputerException{
        switch(computer.wb[0]) {
            case 0:  case 1: case 2:case 3:case 5:case 6:case 8:case 9:case 10: 
            	computer.registerFile[computer.wb[1]]=computer.wb[2]; 
            	break;
           
            case 4:case 7: case 11:  break;
            
           
             
            default: throw new ComputerException("opcode for instruction " + value + " is not valid");
        }
    }


}