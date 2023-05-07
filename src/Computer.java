import java.util.LinkedList;


public class Computer {

    int [] memory ; 
    int instructions_count_in_memory; // number of instructions in memory, to be set while parsing the assembly file 
    int[] registerFile; // R0 to R31
    int PC ;
    int currentCycle;
    
    Instruction Instruction_in_Fetch_Stage;
    Instruction Instruction_in_Decode_Stage;
    Instruction Instruction_in_Execute_Stage;
    Instruction Instruction_in_Memory_Stage;
    Instruction Instruction_in_Writeback_Stage;

    public Computer() {
        memory = new int[2048];
        registerFile = new int[32];
        PC = 0;
        currentCycle = 0 ; 
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
        process_PipeLine();      
        
    }   
    private void process_PipeLine() throws ComputerException {

        if(Instruction_in_Writeback_Stage != null)
        {
            Instruction_in_Writeback_Stage.timeInStage++;
            if(Instruction_in_Writeback_Stage.timeInStage == 1)
            {
                Instruction_in_Writeback_Stage.execute_in_WRITEBACK_stage(this);
                Instruction_in_Writeback_Stage = null;
            }

        }
        
        if(Instruction_in_Memory_Stage != null)
        {
            Instruction_in_Memory_Stage.timeInStage++;
            if(Instruction_in_Memory_Stage.timeInStage == 1)
            {
                Instruction_in_Memory_Stage.execute_in_MEMORY_stage(this);
                Instruction_in_Writeback_Stage = Instruction_in_Memory_Stage;
                Instruction_in_Writeback_Stage.timeInStage = 0;
                Instruction_in_Memory_Stage = null;

            }

        }
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
        if(Instruction_in_Fetch_Stage != null)
        {
            Instruction_in_Fetch_Stage.timeInStage++;
            if(Instruction_in_Fetch_Stage.timeInStage == 1)
            {
                Instruction_in_Fetch_Stage.execute_in_FETCH_stage(this);
                Instruction_in_Decode_Stage = Instruction_in_Fetch_Stage;
                Instruction_in_Decode_Stage.timeInStage = 0;
                Instruction_in_Fetch_Stage = null;

            }

        }
        else
        {
            Instruction_in_Fetch_Stage = peek_Next_Instruction_from_memory();

        }
    }
    public Instruction peek_Next_Instruction_from_memory() {// just reads the next instruction to be fetched without incrementing the PC , pc is incremented in the fetch methods
        if(PC >= instructions_count_in_memory)
            return null;
        return new Instruction(memory[PC]);    
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
}   
