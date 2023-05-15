import java.util.LinkedList;


public class Computer {

    int [] memory ; 
    int instructions_count_in_memory; // number of instructions in memory, to be set while parsing the assembly file 
    int[] registerFile; // R0 to R31
    int PC ;
    int currentCycle;
    int fetchWaitTime;
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
        instructions_already_done_in_pipeline = new Instruction[5];
      //  fetch=new int[5];
       // decode=new int[5];
        memory_Stage_Inputs=new int[3];
        execute_Stage_Inputs=new int[5];
        writeBack_Stage_Inputs=new int[3];
    }

    private void run(String filePath) throws ComputerException{
        loadProgramIntoMemory(filePath);
        int maxClocks = 7 + ( (instructions_count_in_memory-1) * 2 );    
        for(int i = 0 ; i < maxClocks ; i++)
            Tickle_Clock();
        printFinalRequirements();    
    }
    private void loadProgramIntoMemory(String assemblyCode_filePath) {
    }
    private void Tickle_Clock()throws ComputerException {
        currentCycle++;
        //------------- saving old inputs of each stage, as well as old register file and memory
        int[] oldInputsOfExecuteStage = memory_Stage_Inputs.clone();
        int[] oldInputsOfMemoryStage = execute_Stage_Inputs.clone();
        int[] oldInputsOfWriteBackStage = writeBack_Stage_Inputs.clone();
        int[] oldRegisterFile = registerFile.clone();
        int[] oldMemory = memory.clone();     
        //--------------------------
        process_PipeLine();     
        printAfterCycle(oldInputsOfExecuteStage, oldInputsOfMemoryStage, oldInputsOfWriteBackStage, oldRegisterFile, oldMemory);

    }   
    private void process_PipeLine() throws ComputerException {

        if(Instruction_in_Writeback_Stage != null)
        {
            Instruction_in_Writeback_Stage.timeInStage++;
            if(Instruction_in_Writeback_Stage.timeInStage == 1)
            {
                Instruction_in_Writeback_Stage.execute_in_WRITEBACK_stage(this);
                instructions_already_done_in_pipeline[4] = Instruction_in_Writeback_Stage ;
                Instruction_in_Writeback_Stage = null;
            }

        } 
        else
        {
            instructions_already_done_in_pipeline[4] = null ;
        }

        
        if(Instruction_in_Memory_Stage != null )
        {
            Instruction_in_Memory_Stage.timeInStage++;
            if(Instruction_in_Memory_Stage.timeInStage == 1)
            {
                Instruction_in_Memory_Stage.execute_in_MEMORY_stage(this);
                instructions_already_done_in_pipeline[3] = Instruction_in_Memory_Stage ;
                Instruction_in_Writeback_Stage = Instruction_in_Memory_Stage;
                Instruction_in_Writeback_Stage.timeInStage = 0;
                Instruction_in_Memory_Stage = null;

            }

        }
        else
        {
            instructions_already_done_in_pipeline[3] = null ;
        }

        if(Instruction_in_Execute_Stage != null)
        {
            Instruction_in_Execute_Stage.timeInStage++;
            if(Instruction_in_Execute_Stage.timeInStage == 2)
            {
                Instruction_in_Execute_Stage.execute_in_EXECUTE_stage(this);
                instructions_already_done_in_pipeline[2] = Instruction_in_Execute_Stage ;
                Instruction_in_Memory_Stage = Instruction_in_Execute_Stage;
                Instruction_in_Memory_Stage.timeInStage = 0;
                Instruction_in_Execute_Stage = null;

            }

        }
        else
        {
            instructions_already_done_in_pipeline[2] = null ;
        }


        if(Instruction_in_Decode_Stage != null)
        {
            Instruction_in_Decode_Stage.timeInStage++;
            if(Instruction_in_Decode_Stage.timeInStage == 2)
            {
                Instruction_in_Decode_Stage.execute_in_DECODE_stage(this);
                instructions_already_done_in_pipeline[1] = Instruction_in_Decode_Stage ;
                Instruction_in_Execute_Stage = Instruction_in_Decode_Stage;
                Instruction_in_Execute_Stage.timeInStage = 0;
                Instruction_in_Decode_Stage = null;

            }

        }
        else
        {
            instructions_already_done_in_pipeline[1] = null ;
        }


        if(fetchWaitTime==0) 
        {
            fetchWaitTime=1;
            Instruction_in_Fetch_Stage = fetchNextInstruction();
            instructions_already_done_in_pipeline[0] =  Instruction_in_Fetch_Stage ;
            Instruction_in_Decode_Stage=Instruction_in_Fetch_Stage;
            Instruction_in_Fetch_Stage=null;

        }
        else 
        {
            fetchWaitTime--;
            instructions_already_done_in_pipeline[0] = null ;
        }
        
        
    }
    public Instruction fetchNextInstruction() {
        if(PC >= instructions_count_in_memory)
            return null;
        return new Instruction(memory[PC++]);    
    }   
    public void printFinalRequirements() // things that are required to be printed after the last cycle
    {
        System.out.println("Program finished execution after " + currentCycle + ", following are the final values :");
        for(int i = 0 ; i < registerFile.length ; i++)
            System.out.println("R" + i + " : " + registerFile[i]);
        for(int i = 0 ; i < memory.length ; i++)
            System.out.println("Memory" + "[" + i + "]" + " : " + memory[i]);
    }
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
    // to be continued
    public void printAfterCycle(int[] oldInputsOfExecuteStage, int[] oldInputsOfMemoryStage, int[] oldInputsOfWritebackStage, int[] oldRegisterFile, int[] oldMemory) throws ComputerException{
    
        // cycle number
        System.out.println("cycle number : " + currentCycle); // cycle number
        // Which instruction is being executed at each stage
        System.out.println("instruction in fetch stage : " + instructions_already_done_in_pipeline[0]); 
        System.out.println("instruction in decode stage : " + instructions_already_done_in_pipeline[1]);
        System.out.println("instruction in execute stage : " + instructions_already_done_in_pipeline[2]);
        System.out.println("instruction in memory stage : " + instructions_already_done_in_pipeline[3]);
        System.out.println("instruction in writeback stage : " + instructions_already_done_in_pipeline[4]);
        // What are the input parameters/values for each stage
        if(instructions_already_done_in_pipeline[0] != null)
            System.out.println("fetch stage input parameters: PC = " + (PC-1));
        if(instructions_already_done_in_pipeline[1] != null)
            System.out.println("decode stage input parameters: instruction = " + instructions_already_done_in_pipeline[1]);
        if(instructions_already_done_in_pipeline[2] != null)
        {
            if(instructions_already_done_in_pipeline[2].getType().equals("R"))
            {

            }
            else if(instructions_already_done_in_pipeline[2].getType().equals("I"))
            {

            }
            else if(instructions_already_done_in_pipeline[2].getType().equals("J"))
            {

            }
        }
        if(instructions_already_done_in_pipeline[3] != null)
        {
            if(instructions_already_done_in_pipeline[3].getType().equals("R"))
            {

            }
            else if(instructions_already_done_in_pipeline[3].getType().equals("I"))
            {

            }
            else if(instructions_already_done_in_pipeline[3].getType().equals("J"))
            {

            }
        }
        if(instructions_already_done_in_pipeline[4] != null)
        {
            if(instructions_already_done_in_pipeline[4].getType().equals("R"))
            {

            }
            else if(instructions_already_done_in_pipeline[4].getType().equals("I"))
            {

            }
            else if(instructions_already_done_in_pipeline[4].getType().equals("J"))
            {

            }
        }
        // changes in registerFile and memory
        for(int i = 0 ; i < registerFile.length ; i++)
            if(registerFile[i] != oldRegisterFile[i])
                System.out.println("R" + i + "changed from" + oldRegisterFile[i] + " to " + registerFile[i]);
        for(int i = 0 ; i < memory.length ; i++)
            if(memory[i] != oldMemory[i])
                System.out.println("memory " + "[" + i + "] " + "changed from " + oldMemory[i] + " to " + memory[i]);
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
		System.out.println(Long.toBinaryString( Integer.toUnsignedLong(instrans) | 0x100000000L ).substring(1));
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
		instrans+=temp;
		if(temp<0) {
			temp=(temp<<4)>>>4;
		}
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




}   
