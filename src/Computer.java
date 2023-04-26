import java.util.LinkedList;


public class Computer {

    int [] memory ;
    int[] registerFile; // R0 to R31
    int PC ;
    int currentCycle;
    
    // the sole purpose of creating the below 4 lists is to be able to answer questions (c) and (d) in the required printings in the project description
    LinkedList <Integer> indecies_of_updated_Registers; // to be filled in the methods in the Instruction class, to keep track of the registers that have been updated in the current cycle, to print them in printPartialRequirements()
    LinkedList <Integer> old_values_of_updated_Registers; // to be filled in the methods in the Instruction class, to keep track of the values of the registers that have been updated in the current cycle, to print them in printPartialRequirements()
    LinkedList <Integer> indecies_of_updated_Memory_locations; // to keep track of the memory locations that have been updated in the current cycle, to print them in printPartialRequirements()
    LinkedList <Integer> old_values_of_updated_Memory_locations; // to keep track of the values of the memory locations that have been updated in the current cycle, to print them in printPartialRequirements()

    Instruction Instruction_in_Fetch_Stage;
    Instruction Instruction_in_Decode_Stage;
    Instruction Instruction_in_Execute_Stage;
    Instruction Instruction_in_Memory_Stage;
    Instruction Instruction_in_Writeback_Stage;

    public Computer() {
        memory = new int[1024];
        registerFile = new int[32];
        PC = 0;
        currentCycle = 0 ; 
        indecies_of_updated_Registers = new LinkedList<Integer>();
        old_values_of_updated_Registers = new LinkedList<Integer>();
        indecies_of_updated_Memory_locations = new LinkedList<Integer>();
        old_values_of_updated_Memory_locations = new LinkedList<Integer>();
    }

    private void Tickle_Clock()throws ComputerException {
        currentCycle++;
        process_PipeLine();      
        printPartialRequirements();
        indecies_of_updated_Registers.clear();
        old_values_of_updated_Registers.clear();
        indecies_of_updated_Memory_locations.clear();
        old_values_of_updated_Memory_locations.clear();
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
    private void printPartialRequirements() // things that are required to be printed after each single cycle
    {
       //PrintWriter pw = new PrintWriter(System.out); // 

    }
    public Instruction peek_Next_Instruction_from_memory() {// just reeds the next instruction to be fetched without incrementing the PC
        if(PC > 1023)
            return null;
        return new Instruction(memory[PC]);    
    }   
    public int getRegister(int index) {
        return registerFile[index];
    }
    public void setRegister(int index, int value) {
        if(index != 0) // to prevent writing into the ZERO register
            registerFile[index] = value;
    }
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}   
