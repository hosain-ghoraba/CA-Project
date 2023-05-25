import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Random;

public class Computer {

    int [] memory ; 
    int instructions_count_in_memory; // number of instructions in memory, to be set while parsing the assembly file 
    int[] registerFile; // R0 to R31
    int PC ;
    int currentCycle;
    int fetchWaitTime;
    int instructionpipelined;
    int oldvalue;
    
    Instruction[] instructions_already_done_in_pipeline;
    int instrans; //instruction to be translated
    
    Instruction Instruction_in_Fetch_Stage;
    Instruction Instruction_in_Decode_Stage;
    Instruction Instruction_in_Execute_Stage;
    Instruction Instruction_in_Memory_Stage;
    Instruction Instruction_in_Writeback_Stage;
    //int[] fetch;
    //int[] decode;
    int[] execute_Stage_Inputs;
    int[] memory_Stage_Inputs;
    int[] writeBack_Stage_Inputs;
    

    public Computer() {
        memory = new int[2048];
        registerFile = new int[32];
        PC = 0;
        currentCycle = 0 ;
        fetchWaitTime = 0;
        oldvalue=-1;
        instructions_already_done_in_pipeline = new Instruction[5];
      //  fetch=new int[5];
       // decode=new int[5];
        memory_Stage_Inputs=new int[3];
        execute_Stage_Inputs=new int[5];
        writeBack_Stage_Inputs=new int[3];
        instructionpipelined=0;
    }

    private void run(String filePath) throws ComputerException, IOException{
        loadProgramIntoMemory(filePath);   
        while(true) 
        {
        Tickle_Clock();   
        if(Instruction_in_Fetch_Stage==null && Instruction_in_Decode_Stage==null && Instruction_in_Execute_Stage==null && Instruction_in_Memory_Stage==null && Instruction_in_Writeback_Stage==null)
            break;
        }
        printFinalRequirements();
    }
    
    private void Tickle_Clock()throws ComputerException {
        currentCycle++;
        //------------- saving old inputs of each stage, as well as old register file and memory
        int[] oldInputsOfExecuteStage = execute_Stage_Inputs.clone();
        int[] oldInputsOfMemoryStage = memory_Stage_Inputs.clone();
        int[] oldInputsOfWriteBackStage = writeBack_Stage_Inputs.clone();
        int[] oldRegisterFile = registerFile.clone();
        int[] oldMemory = memory.clone();     
        //--------------------------
        process_PipeLine();     
       printAfterCycle(oldInputsOfExecuteStage, oldInputsOfMemoryStage, oldInputsOfWriteBackStage, oldRegisterFile, oldMemory);

    }   
    private void process_PipeLine() throws ComputerException {

        instructions_already_done_in_pipeline[4] = Instruction_in_Writeback_Stage ;
        if(Instruction_in_Writeback_Stage != null)
       {        
            Instruction_in_Writeback_Stage.timeInStage++;
            if(Instruction_in_Writeback_Stage.timeInStage == 1)
            {
                Instruction_in_Writeback_Stage.execute_in_WRITEBACK_stage(this);   
                Instruction_in_Writeback_Stage = null;
            }

        } 
        
        instructions_already_done_in_pipeline[3] = Instruction_in_Memory_Stage ;
        if(Instruction_in_Memory_Stage != null )
        {
            Instruction_in_Memory_Stage.timeInStage++;
            if(Instruction_in_Memory_Stage.timeInStage == 1)
            {
                if(Instruction_in_Memory_Stage.getopcode()==7) {//handling flush of instruction in case of jump
             	   Instruction_in_Decode_Stage=null;
             	   Instruction_in_Execute_Stage=null;
             	   //Instruction_in_Memory_Stage=null; 	   
                }
                if(Instruction_in_Memory_Stage.getopcode()==4) {
             	   if(Instruction_in_Memory_Stage.branch) {
             		   Instruction_in_Decode_Stage=null;
                 	   Instruction_in_Execute_Stage=null;
             	   }
                }
                Instruction_in_Memory_Stage.execute_in_MEMORY_stage(this);
                Instruction_in_Writeback_Stage = Instruction_in_Memory_Stage;
                Instruction_in_Writeback_Stage.timeInStage = 0;
                Instruction_in_Memory_Stage = null;

            }

        }

        instructions_already_done_in_pipeline[2] = Instruction_in_Execute_Stage ;
        if(Instruction_in_Execute_Stage != null)
        {
            Instruction_in_Execute_Stage.timeInStage++;
            if(Instruction_in_Execute_Stage.timeInStage == 2)
            {
                Instruction_in_Execute_Stage.execute_in_EXECUTE_stage(this);
                Instruction_in_Memory_Stage = Instruction_in_Execute_Stage;
                Instruction_in_Memory_Stage.timeInStage = 0;
                Instruction_in_Execute_Stage = null;
            }
        }

        instructions_already_done_in_pipeline[1] = Instruction_in_Decode_Stage ;
        if(Instruction_in_Decode_Stage != null)
        {
            Instruction_in_Decode_Stage.timeInStage++;
            if(Instruction_in_Decode_Stage.timeInStage == 2)
            {
                Instruction_in_Decode_Stage.execute_in_DECODE_stage(this);
                Instruction_in_Execute_Stage = Instruction_in_Decode_Stage;
                Instruction_in_Execute_Stage.timeInStage = 0;
                Instruction_in_Decode_Stage = null;

            }

        }
        

        if(fetchWaitTime==0) 
        {
        	fetchWaitTime=1;
            Instruction_in_Fetch_Stage = fetchNextInstruction();
            instructions_already_done_in_pipeline[0] =  Instruction_in_Fetch_Stage ;
            Instruction_in_Decode_Stage=Instruction_in_Fetch_Stage;
            if(Instruction_in_Decode_Stage != null)
            	Instruction_in_Decode_Stage.timeInStage = 0;
            Instruction_in_Fetch_Stage=null;

        }
        else 
        {
            fetchWaitTime--;
            instructions_already_done_in_pipeline[0] = null;
        }
        
        
    }
    public Instruction fetchNextInstruction() throws ComputerException {

        if(oldvalue==-1)
        {
            if(PC >= instructions_count_in_memory)
                return null;
            instructionpipelined++;
            return new Instruction(memory[PC++]);   
        }
        else 
        {
            if(oldvalue+1 >= instructions_count_in_memory)
                return null;
            Instruction x= new Instruction(memory[oldvalue+1]);   
            oldvalue=-1; 
            instructionpipelined++;
            return x;
        }

    }   
    public void printFinalRequirements() // things that are required to be printed after the last cycle
    {
        System.out.println("\nEND OF PROGRAM ---------------------------------------------------------------------------------------------------------------------");
        System.out.println("Program finished execution after " + currentCycle + " cycles, following are the final values :");
        System.out.println("instuction count in memory = " + instructions_count_in_memory);
        System.out.println("instructions entered pipeline = " + instructionpipelined);
        System.out.println("PC : " + PC);
        for(int i = 0 ; i < registerFile.length ; i++)
            System.out.println("R" + i + " : " + registerFile[i]);
        // for(int i = 0 ; i < memory.length ; i++)
        //     System.out.println("Memory" + "[" + i + "]" + " : " + memory[i]);
    }

    public void printAfterCycle(int[] oldInputsOfExecuteStage, int[] oldInputsOfMemoryStage, int[] oldInputsOfWritebackStage, int[] oldRegisterFile, int[] oldMemory) throws ComputerException{
    
        // cycle number
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("cycle number : " + currentCycle); // cycle number
        // Which instruction is being executed at each stage
        
        System.out.println("instruction in fetch stage : " + instructions_already_done_in_pipeline[0]); 
        System.out.print("instruction in decode stage : " + instructions_already_done_in_pipeline[1]);
        if(instructions_already_done_in_pipeline[1] != null)
        {
            int decodeTime = instructions_already_done_in_pipeline[1].timeInStage == 1 ? 1 : 2; 
            System.out.print(" , time in stage : " + decodeTime);
        }
        System.out.print("\ninstruction in execute stage : " + instructions_already_done_in_pipeline[2]);
        if(instructions_already_done_in_pipeline[2] != null)
        {
            int executeTime = instructions_already_done_in_pipeline[2].timeInStage == 1 ? 1 : 2;
            System.out.print(" , time in stage : " + executeTime);
        }
        System.out.println("\ninstruction in memory stage : " + instructions_already_done_in_pipeline[3]);
        System.out.println("instruction in writeback stage : " + instructions_already_done_in_pipeline[4]);

        // What are the inputs/outputs for each stage
        System.out.println("--------------------------------");
        System.out.println("FETCH stage inputs / outputs : ");
        if(instructions_already_done_in_pipeline[0] != null)
        {
            System.out.println("inputs : PC = " + (PC-1));
            System.out.println("outputs : an instruction = " + instructions_already_done_in_pipeline[0]);
        }

        System.out.println("--------------------------------");
        System.out.println("DECODE stage inputs / outputs : ");
        if(instructions_already_done_in_pipeline[1] != null)
        {
            System.out.println("inputs : an instruction = " + instructions_already_done_in_pipeline[1]);
            if(instructions_already_done_in_pipeline[1].timeInStage == 0)
            {
                System.out.print("outputs : opcode = " + execute_Stage_Inputs[0] + ", ");
                String type = instructions_already_done_in_pipeline[1].getType();
                switch(type)
                {
                    case "R":
                        System.out.print("r1 = " + execute_Stage_Inputs[1] + ", ");
                        System.out.print("r2 = " + execute_Stage_Inputs[2] + ", ");
                        System.out.print("r3 = " + execute_Stage_Inputs[3] + ", ");
                        System.out.println("shamt = " + execute_Stage_Inputs[4]);
                        break;
                    case "I":
                        System.out.print("r1 = " + execute_Stage_Inputs[1] + ", ");
                        System.out.print("r2 = " + execute_Stage_Inputs[2] + ", ");
                        System.out.println("immediate = " + execute_Stage_Inputs[3]);
                        break;
                    case "J":
                        System.out.println("address = " + execute_Stage_Inputs[1]);
                        break;
                }
            }
            else
                System.out.println("outputs are not available yet");
        }

        System.out.println("--------------------------------");
        System.out.println("EXECUTE stage inputs / outputs : ");
        if(instructions_already_done_in_pipeline[2] != null)
        {
            System.out.print("inputs : ALU Function (opcode) = " + oldInputsOfExecuteStage[0] + ", ");
            String type = instructions_already_done_in_pipeline[2].getType();
            switch(type)
            {
                case "R": 
                    System.out.print("ALU src 1 (normal register value) = " + oldInputsOfExecuteStage[2] + ", ");
                    System.out.println("ALU src 2 (normal register value) = " + oldInputsOfExecuteStage[3]);
                    if(instructions_already_done_in_pipeline[2].timeInStage == 0)
                    {
                        System.out.println("outputs : ALU result = " + memory_Stage_Inputs[2]);
                    }
                    else
                        System.out.println("outputs are not available yet");
                    break;
                case "I":
                    System.out.print("ALU src 1 (normal register value) = " + oldInputsOfExecuteStage[2] + ", ");
                    System.out.println("ALU src 2 (immediate value) = " + oldInputsOfExecuteStage[3]);
                    if(instructions_already_done_in_pipeline[2].timeInStage == 0)
                    {
                        System.out.println("outputs : ALU result = " + memory_Stage_Inputs[2]);
                    }
                    else
                        System.out.println("outputs are not available yet");
                    break;
                case "J":
                    System.out.print("inputs : ");
                    System.out.print("ALU src1 (offset) = " + oldInputsOfExecuteStage[1]);
                    System.out.println(" , ALU src2 (PC) = " + oldInputsOfExecuteStage[2]);
                    if(instructions_already_done_in_pipeline[2].timeInStage == 0)
                    {
                        int addrssToJumpTo=PC;
                        System.out.println("outputs : ALU result(address to jump to) = " + addrssToJumpTo);
                    }
                    else                      
                        System.out.println("outputs are not available yet");                     
                    break;

            }
            }
        
        System.out.println("--------------------------------");
        System.out.println("MEMORY stage inputs / outputs : ");
        if(instructions_already_done_in_pipeline[3] != null)
        {
            String type = instructions_already_done_in_pipeline[3].getType();
            switch(type)
            {
                case "R":
                    System.out.println("inputs : nothing , this is a R type instruction");
                    System.out.println("outputs : nothing , this is a R type instruction");
                    break;
                case "I":
                	 int op= instructions_already_done_in_pipeline[3].getopcode();
                	 if(op==11) 
                     {
                        System.out.print("inputs : ");
                        System.out.print("memory address to be written into :  " + oldInputsOfMemoryStage[2] + ", ");
                        System.out.println("value to be written is:  " + oldInputsOfMemoryStage[1]);
                        System.out.println("outputs : nothing(since I am writing into memory" );
                     }
                	 else if(op==10)
                     {
                        System.out.println("inputs : memory address to be read from : " + oldInputsOfMemoryStage[2]);
                        System.out.println("outputs : read value from " + "Memory[" +oldInputsOfMemoryStage[2]+ "] is " + writeBack_Stage_Inputs[2] );                      
                     }
                	 else 
                     {
                         System.out.println("inputs : nothing , this is a I type instruction with with opcode " + op + " which doesn't perfrom any writing into memory");
                         System.out.println("outputs : (there is no actual outputs but it is from execute stage, it is just passing by the memory on its way) : "+ "register to be written into : R"+writeBack_Stage_Inputs[1]+" , value to be written is:  "+ writeBack_Stage_Inputs[2]);         		 
                	 }              
                     break;
                case "J":
                     System.out.println("inputs : nothing , this is a J type instruction");
                     System.out.println("outputs : nothing , this is a J type instruction");
                     break;
            }
        }
        System.out.println("--------------------------------");
        System.out.println("WRITEBACK stage inputs / outputs : ");
        if(instructions_already_done_in_pipeline[4] != null)
        {
            String type = instructions_already_done_in_pipeline[4].getType();
            switch(type)
            {
                case "R" :
                     System.out.println("inputs : register to be written into : R"+oldInputsOfWritebackStage[1]);
                     break;
                case "I" :
                     int op= instructions_already_done_in_pipeline[4].getopcode();
                     if(op==3 || op==6 || op==10) 
                     {
                       System.out.print("inputs : ");
                	   System.out.print("register to be written into : R"+oldInputsOfWritebackStage[1]);
                       System.out.println(" , value to be written is:  "+ oldInputsOfWritebackStage[2]);
                     }
                     if(op==4 || op==11) 
                     {
                        System.out.println("inputs : nothing , this is a I type instruction with with opcode " + op + " which doesn't perfrom a writeback");
                     }
                     break; 
                case "J" :
                     System.out.println("inputs : nothing , this is a J type instruction");
                     break;
            }
            System.out.println("outputs : " + getRandomFunMessage());
        }
        System.out.println("----------------------------------------------");
        System.out.println("changes in registerFile and Memory :");
        for(int i = 0 ; i < registerFile.length ; i++)
            if(registerFile[i] != oldRegisterFile[i])
                System.out.println("R" + i + " changed from " + oldRegisterFile[i] + " to " + registerFile[i] + " in writeback stage");
        for(int i = 0 ; i < memory.length ; i++)
            if(memory[i] != oldMemory[i])
                System.out.println("memory " + "[" + i + "] " + " changed from " + oldMemory[i] + " to " + memory[i] + " in memory stage");
    }
    private void loadProgramIntoMemory(String assemblyCode_filePath) throws IOException {
    	BufferedReader br = new BufferedReader(new FileReader(assemblyCode_filePath));
        String line = br.readLine();
        while (line != null && !line.equals("")) 
        {
            if(line.charAt(0) != '/') // if it is not a comment
            {
                trans(line);
                System.out.println("instruction " + line + "   is translated to " + instrans + " and added to memory");
                memory[instructions_count_in_memory++] = instrans;
            }
            line = br.readLine();
        }
        br.close();
        
    }
    public void trans(String s){
		s=s.toUpperCase();
    	String arr[]= s.split(" ", 2);
		instrans=0;
		getOpcode(arr[0],arr[1]);
		
		
	}
	public void getOpcode(String s,String s1) {
		int temp;
		
		switch(s) {
		case "ADD":instrans+=0b00000000000000000000000000000000;
		String arr[]= s1.split(" ",3);
		getreg1(arr[0]);
		getreg2(arr[1]);
		getreg3(arr[2]);		
		break;
		
		case "SUB":instrans+=0b00010000000000000000000000000000;
		String arrs[]= s1.split(" ",3);
		getreg1(arrs[0]);
		getreg2(arrs[1]);
		getreg3(arrs[2]);
		break;
		
		case "MUL":instrans+=0b00100000000000000000000000000000;
		String arrm[]= s1.split(" ",3);
		getreg1(arrm[0]);
		getreg2(arrm[1]);
		getreg3(arrm[2]);break;
		
		case "MOVI":instrans+=0b00110000000000000000000000000000;
		String arrmi[]= s1.split(" ",2);
		getreg1(arrmi[0]);
		// System.out.println(Long.toBinaryString( Integer.toUnsignedLong(instrans) | 0x100000000L ).substring(1));
		temp=Integer.parseInt(arrmi[1]);//not working adding value is not correct
		if(temp<0) {
			temp=(temp<<14)>>>14;
		}
		instrans+=temp;break;
		
		case "JEQ":instrans+=0b01000000000000000000000000000000;
		String arrjeq[]= s1.split(" ",3);
		getreg1(arrjeq[0]);
		getreg2(arrjeq[1]);
		temp=Integer.parseInt(arrjeq[2]);
		if(temp<0) {
			temp=(temp<<14)>>>14;
		}
		
		instrans+=temp;break;
		
		case "AND":instrans+=0b01010000000000000000000000000000;
		String arrand[]= s1.split(" ",3);
		getreg1(arrand[0]);
		getreg2(arrand[1]);
		getreg3(arrand[2]);break;
		case "XORI":instrans+=0b01100000000000000000000000000000;
		String arrx[]= s1.split(" ",3);
		getreg1(arrx[0]);
		getreg2(arrx[1]);
		temp=Integer.parseInt(arrx[2]);
		if(temp<0) {
			temp=(temp<<14)>>>14;
		}
		instrans+=temp;
		break;
		case "JMP":instrans+=0b01110000000000000000000000000000;
		temp=Integer.parseInt(s1);
		
		if(temp<0) {
			temp=(temp<<4)>>>4;
		}
		instrans+=temp;
		break;
		case "LSL":instrans+=0b10000000000000000000000000000000;
		String arrlsl[]= s1.split(" ",3);
		getreg1(arrlsl[0]);
		getreg2(arrlsl[1]);
		temp=Integer.parseInt(arrlsl[2]);
		instrans+=temp;
		break;
		case "LSR":instrans+=0b10010000000000000000000000000000;
		String arrlsr[]= s1.split(" ",3);
		getreg1(arrlsr[0]);
		getreg2(arrlsr[1]);
		temp=Integer.parseInt(arrlsr[2]);
		instrans+=temp;
		break;
		case "MOVR":instrans+=0b10100000000000000000000000000000;
		String arrmr[]= s1.split(" ",3);
		getreg1(arrmr[0]);
		getreg2(arrmr[1]);
		temp=Integer.parseInt(arrmr[2]);
		instrans+=temp;break;
		case "MOVM":instrans+=0b10110000000000000000000000000000;
		String arrmm[]= s1.split(" ",3);
		getreg1(arrmm[0]);
		getreg2(arrmm[1]);
		temp=Integer.parseInt(arrmm[2]);
		instrans+=temp;break;
		default:
			
		}
		
		
		
	}
	public void getreg1(String s) {
		
		switch(s) {
		case "R0":instrans+=0b00000000000000000000000000000000;break;
		case "R1":instrans+=0b00000000100000000000000000000000;break;
		case "R2":instrans+=0b00000001000000000000000000000000;break;
		case "R3":instrans+=0b00000001100000000000000000000000;break;
		case "R4":instrans+=0b00000010000000000000000000000000;break;
		case "R5":instrans+=0b00000010100000000000000000000000;break;
		case "R6":instrans+=0b00000011000000000000000000000000;break;
		case "R7":instrans+=0b00000011100000000000000000000000;break;
		case "R8":instrans+=0b00000100000000000000000000000000;break;
		case "R9":instrans+=0b00000100100000000000000000000000;break;
		case "R10":instrans+=0b00000101000000000000000000000000;break;
		case "R11":instrans+=0b00000101100000000000000000000000;break;
		case "R12":instrans+=0b00000110000000000000000000000000;break;
		case "R13":instrans+=0b00000110100000000000000000000000;break;
		case "R14":instrans+=0b00000111000000000000000000000000;break;
		case "R15":instrans+=0b00000111100000000000000000000000;break;
		case "R16":instrans+=0b00001000000000000000000000000000;break;
		case "R17":instrans+=0b00001000100000000000000000000000;break;
		case "R18":instrans+=0b00001001000000000000000000000000;break;
		case "R19":instrans+=0b00001001100000000000000000000000;break;
		case "R20":instrans+=0b00001010000000000000000000000000;break;
		case "R21":instrans+=0b00001010100000000000000000000000;break;
		case "R22":instrans+=0b00001011000000000000000000000000;break;
		case "R23":instrans+=0b00001011100000000000000000000000;break;
		case "R24":instrans+=0b00001100000000000000000000000000;break;
		case "R25":instrans+=0b00001100100000000000000000000000;break;
		case "R26":instrans+=0b00001101000000000000000000000000;break;
		case "R27":instrans+=0b00001101100000000000000000000000;break;
		case "R28":instrans+=0b00001110000000000000000000000000;break;
		case "R29":instrans+=0b00001110100000000000000000000000;break;
		case "R30":instrans+=0b00001111000000000000000000000000;break;
		case "R31":instrans+=0b00001111100000000000000000000000;break;
		}
		
	}
    public void getreg2(String s) {
		
		switch(s) {
		case "R0":instrans+=(0b00000000000000000000000000000000)>>5;break;
		case "R1":instrans+=(0b00000000100000000000000000000000)>>5;break;
		case "R2":instrans+=(0b00000001000000000000000000000000)>>5;break;
		case "R3":instrans+=(0b00000001100000000000000000000000)>>5;break;
		case "R4":instrans+=(0b00000010000000000000000000000000)>>5;break;
		case "R5":instrans+=(0b00000010100000000000000000000000)>>5;break;
		case "R6":instrans+=(0b00000011000000000000000000000000)>>5;break;
		case "R7":instrans+=(0b00000011100000000000000000000000)>>5;break;
		case "R8":instrans+=(0b00000100000000000000000000000000)>>5;break;
		case "R9":instrans+=(0b00000100100000000000000000000000)>>5;break;
		case "R10":instrans+=(0b00000101000000000000000000000000)>>5;break;
		case "R11":instrans+=(0b00000101100000000000000000000000)>>5;break;
		case "R12":instrans+=(0b00000110000000000000000000000000)>>5;break;
		case "R13":instrans+=(0b00000110100000000000000000000000)>>5;break;
		case "R14":instrans+=(0b00000111000000000000000000000000)>>5;break;
		case "R15":instrans+=(0b00000111100000000000000000000000)>>5;break;
		case "R16":instrans+=(0b00001000000000000000000000000000)>>5;break;
		case "R17":instrans+=(0b00001000100000000000000000000000)>>5;break;
		case "R18":instrans+=(0b00001001000000000000000000000000)>>5;break;
		case "R19":instrans+=(0b00001001100000000000000000000000)>>5;break;
		case "R20":instrans+=(0b00001010000000000000000000000000)>>5;break;
		case "R21":instrans+=(0b00001010100000000000000000000000)>>5;break;
		case "R22":instrans+=(0b00001011000000000000000000000000)>>5;break;
		case "R23":instrans+=(0b00001011100000000000000000000000)>>5;break;
		case "R24":instrans+=(0b00001100000000000000000000000000)>>5;break;
		case "R25":instrans+=(0b00001100100000000000000000000000)>>5;break;
		case "R26":instrans+=(0b00001101000000000000000000000000)>>5;break;
		case "R27":instrans+=(0b00001101100000000000000000000000)>>5;break;
		case "R28":instrans+=(0b00001110000000000000000000000000)>>5;break;
		case "R29":instrans+=(0b00001110100000000000000000000000)>>5;break;
		case "R30":instrans+=(0b00001111000000000000000000000000)>>5;break;
		case "R31":instrans+=(0b00001111100000000000000000000000)>>5;break;
		}
		
	}
    public void getreg3(String s) {
	
	switch(s) {
	case "R0":instrans+=(0b00000000000000000000000000000000)>>10;break;
	case "R1":instrans+=(0b00000000100000000000000000000000)>>10;break;
	case "R2":instrans+=(0b00000001000000000000000000000000)>>10;break;
	case "R3":instrans+=(0b00000001100000000000000000000000)>>10;break;
	case "R4":instrans+=(0b00000010000000000000000000000000)>>10;break;
	case "R5":instrans+=(0b00000010100000000000000000000000)>>10;break;
	case "R6":instrans+=(0b00000011000000000000000000000000)>>10;break;
	case "R7":instrans+=(0b00000011100000000000000000000000)>>10;break;
	case "R8":instrans+=(0b00000100000000000000000000000000)>>10;break;
	case "R9":instrans+=(0b00000100100000000000000000000000)>>10;break;
	case "R10":instrans+=(0b00000101000000000000000000000000)>>10;break;
	case "R11":instrans+=(0b00000101100000000000000000000000)>>10;break;
	case "R12":instrans+=(0b00000110000000000000000000000000)>>10;break;
	case "R13":instrans+=(0b00000110100000000000000000000000)>>10;break;
	case "R14":instrans+=(0b00000111000000000000000000000000)>>10;break;
	case "R15":instrans+=(0b00000111100000000000000000000000)>>10;break;
	case "R16":instrans+=(0b00001000000000000000000000000000)>>10;break;
	case "R17":instrans+=(0b00001000100000000000000000000000)>>10;break;
	case "R18":instrans+=(0b00001001000000000000000000000000)>>10;break;
	case "R19":instrans+=(0b00001001100000000000000000000000)>>10;break;
	case "R20":instrans+=(0b00001010000000000000000000000000)>>10;break;
	case "R21":instrans+=(0b00001010100000000000000000000000)>>10;break;
	case "R22":instrans+=(0b00001011000000000000000000000000)>>10;break;
	case "R23":instrans+=(0b00001011100000000000000000000000)>>10;break;
	case "R24":instrans+=(0b00001100000000000000000000000000)>>10;break;
	case "R25":instrans+=(0b00001100100000000000000000000000)>>10;break;
	case "R26":instrans+=(0b00001101000000000000000000000000)>>10;break;
	case "R27":instrans+=(0b00001101100000000000000000000000)>>10;break;
	case "R28":instrans+=(0b00001110000000000000000000000000)>>10;break;
	case "R29":instrans+=(0b00001110100000000000000000000000)>>10;break;
	case "R30":instrans+=(0b00001111000000000000000000000000)>>10;break;
	case "R31":instrans+=(0b00001111100000000000000000000000)>>10;break;
	}
	
}
    public String getRandomFunMessage(){
        String[] messages = new String[5];
        messages[0] = "(instruction) : now lets go to the next sta..... AAAAAAAHHHH NOOOO I AM BEING FLUSHED !!";
        messages[1] = "(instruction) : 'hooooof !! I am finally free from this world !! these humans treat me as a mere piece of data ! '" ;
        messages[2] = "(instruction) : finally out of this torture machine !! they are cutting me into pieces !! " ;
        messages[3] = "(instruction) : goodbye crazy humans !! I hope not to see you again !! " ;
        messages[4] = "(instruction) : now I am finally free !! but...wait...what am I going to do now ? how can a piece of data spent its life ? " ;
        return messages[currentCycle % messages.length];

    }
    public static void main(String[] args) throws ComputerException, IOException {

        Computer computer = new Computer();
        computer.run("assembly.txt");
    
}
}   

