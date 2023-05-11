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
        res=getRegister(computer.execute[2], computer) + getRegister(computer.execute[2], computer);
        computer.memorystage[1]=computer.execute[1];
        computer.memorystage[2]=res;
        break;
        
        case 1: 
        res=getRegister(computer.execute[2], computer) -getRegister(computer.execute[2], computer);
        computer.memorystage[1]=res;
        break;
        
        case 2: 
        res=getRegister(computer.execute[2], computer) * getRegister(computer.execute[2], computer);
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
        switch(getopcode()) {
            case 0:  case 1: case 2:case 3:case 5:case 6:case 8:case 9:case 10: 
            	computer.registerFile[computer.wb[1]]=computer.wb[2]; 
            	break;
           
            case 4:case 7: case 11:  break;
            
           
             
            default: throw new ComputerException("opcode for instruction " + value + " is not valid");
        }
    }


    // ADD 
    public void FETCH_ADD(Computer computer) {
     DECODE_ADD(computer);
        

    }
    public void DECODE_ADD(Computer computer) {
    
    	int rd= ((value<<4)>>27);
    	int rs1=(value<<9)>>27;
    	int rs2=(value<<14)>>27;
    	EXECUTE_ADD(rd,rs1,rs2,computer);
        

    }
    public void EXECUTE_ADD(int rd,int rs1,int rs2,Computer computer) {
    	int rdval=getRegister(rs1, computer) + getRegister(rs2, computer);
    	setRegister(rd, rdval, computer);
        

    }
    public void MEMORY_ADD(Computer computer) {
        

    }
    public void WRITEBACK_ADD(Computer computer) {
        

    }

    // SUB
    public void FETCH_SUB(Computer computer) {
        

    }
    public void DECODE_SUB(Computer computer) {
    	int rd= ((value<<4)>>27);
    	int rs1=(value<<9)>>27;
    	int rs2=(value<<14)>>27;
    	EXECUTE_SUB(rd,rs1,rs2,computer);

    }
    public void EXECUTE_SUB(int rd,int rs1,int rs2,Computer computer) {
    	int rdval=getRegister(rs1, computer) - getRegister(rs2, computer);
    	setRegister(rd, rdval, computer);

    }
    //hello
    public void MEMORY_SUB(Computer computer) {
        

    }
    public void WRITEBACK_SUB(Computer computer) {
        

    }

    // MUL
    public void FETCH_MUL(Computer computer) {
    	int rd= ((value<<4)>>27);
    	int rs1=(value<<9)>>27;
    	int rs2=(value<<14)>>27;
    	EXECUTE_MUL(rd,rs1,rs2,computer);

    }
    public void DECODE_MUL(Computer computer) {
        

    }
    public void EXECUTE_MUL(int rd,int rs1,int rs2,Computer computer) {
    	int rdval=getRegister(rs1, computer) *getRegister(rs2, computer);
    	setRegister(rd, rdval, computer);

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
    	int r1= ((value<<4)>>27);
    	int r2=(value<<9)>>27;
    	int jump=(value<<14)>>14;
    	EXECUTE_JEQ(r1,r2,jump,computer);

    }
    public void EXECUTE_JEQ(int r1,int r2,int j,Computer computer) {
        if(r1==r2) {
        	computer.PC=computer.PC+1+j;
        	
        }

    }
    public void MEMORY_JEQ(Computer computer) {
        

    }
    public void WRITEBACK_JEQ(Computer computer) {
        

    }

    // AND
    public void FETCH_AND(Computer computer) {
    	


    }
    public void DECODE_AND(Computer computer) {
    	int rd= ((value<<4)>>27);
    	int rs1=(value<<9)>>27;
    	int rs2=(value<<14)>>27;
    	EXECUTE_AND(rd,rs1,rs2,computer);

    }
    public void EXECUTE_AND(int rd,int rs1,int rs2,Computer computer) {
    	int rdval=getRegister(rs1, computer) &getRegister(rs2, computer);
    	setRegister(rd, rdval, computer);


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
    	DECODE_JMP(computer);
        

    }
    public void DECODE_JMP(Computer computer) {
    	EXECUTE_JMP(computer);
        

    }
    public void EXECUTE_JMP(Computer computer) {
    	computer.PC=((computer.PC>>28)<<28) + ((value<<4)>>4);
    	

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
    	int r1= ((value<<4)>>27);
    	int r2=(value<<9)>>27;
    	int address=(value<<14)>>14;
    	EXECUTE_JEQ(r1,r2,address,computer);


    }
    public void EXECUTE_MOVR(int r1,int r2,int a,Computer computer) {
        int address=r2+a;
        MEMORY_MOVR(r1,address,computer);

    }
    public void MEMORY_MOVR(int r1,int address ,Computer computer) {
        int x= computer.memory[address];
        WRITEBACK_MOVR( r1,x,computer);

    }
    public void WRITEBACK_MOVR(int r1,int x,Computer computer) {
        computer.registerFile[r1]=x;

    }

    // MOVM
    public void FETCH_MOVM(Computer computer) {
        

    }
    public void DECODE_MOVM(Computer computer) {
    	int r1= ((value<<4)>>27);
    	int r2=(value<<9)>>27;
    	int address=(value<<14)>>14;
    	EXECUTE_JEQ(r1,r2,address,computer);

    }
    public void EXECUTE_MOVM(int r1,int r2,int a,Computer computer) {
    	int address=r2+a;
    	MEMORY_MOVM(r1,address,computer);
    }
    public void MEMORY_MOVM(int r1,int address ,Computer computer) {
        computer.memory[address]=r1;

    }
    public void WRITEBACK_MOVM(Computer computer) {
        

    }
    


    

    
    
}
