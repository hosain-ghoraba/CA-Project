import java.util.LinkedList;


public class Computer {

    int [] memory ; 
    int instructions_count_in_memory; // number of instructions in memory, to be set while parsing the assembly file 
    int[] registerFile; // R0 to R31
    int PC ;
    int currentCycle;
    int fetchWaitTime;
    Instruction[] instructions_already_done_in_pipeline;
    
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
}   
