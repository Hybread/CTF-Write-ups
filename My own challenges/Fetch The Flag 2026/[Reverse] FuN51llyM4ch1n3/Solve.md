# Solve

## Initial Recon
Given a single executable named `G00fyF1NM4Ch1N3.exe`. Running the binary executes something but not significant to the solve itself. 
We can check the binary details with DiE to see if we can find anything to note down: 

<img width="411" height="284" alt="image" src="https://github.com/user-attachments/assets/cbc8ac89-e1d9-4f8a-80ce-d7e48a4c48b8" />

Seems like a simple binary but for some reason, it’s 10MB in size. Quite large for a C program. 
If we attempt to execute the binary in a terminal, we get the following: 

<img width="569" height="37" alt="image" src="https://github.com/user-attachments/assets/96ff3ddd-f583-4d44-a393-924e2bbd0ac7" />

Seems like it requires a 16 char password. Let’s try entering an input with the length of 16 chars: 

<img width="582" height="45" alt="image" src="https://github.com/user-attachments/assets/657287c0-c4ee-40f7-9258-4d66f9dba5ad" />

Let’s open it up in IDA and see what we’re dealing with: 

<img width="898" height="236" alt="image" src="https://github.com/user-attachments/assets/d4fade99-bd9f-446b-ab34-f5abed8e7a84" />

In the decompilation, we see a huge function table. We also see a bunch of `state_x` functions and in the main functions, we can see other functions such as `g_position`, `g_state`, `g_transition`, and `g_input`. Based on this, and the challenge name itself, it seems like we’re dealing with some sort of Finite State Machine. 

<img width="573" height="700" alt="image" src="https://github.com/user-attachments/assets/bead95aa-1585-4a3d-b35e-630c8a2470bc" />

We can see in the pseudocode that the win condition is just to provide a 16-character string where each character causes a valid transition in the state machine. Each of those 16 transitions must successfully increment `g_transitions`. 

We can also see that the starting state is calculated by XORing the first character of our input with 0x1337 and taking the modulo of the total state space (96,767). 

`g_state = (0x1337 ⊕ input[0])(mod 96767)`

In each iteration of the program, it executes a function pointer from a massive array called `STATES` based on the current `g_state`. 

<img width="582" height="361" alt="image" src="https://github.com/user-attachments/assets/1533bd61-4bfa-4e9a-9dde-41204bfc4ea9" />

## State Machine Mechanics 
The core of this challenge lies in the `STATES` array. Each entry is a pointer to a unique function representing a specific state. 

When we analyze one of the states, we see the following: 

<img width="289" height="290" alt="image" src="https://github.com/user-attachments/assets/807bdb2a-aad0-4089-ab52-df517d1d1c30" />

We see that in this state, it fetches the character at the current input position using `get_current_input()`, then compares that character against one or more hardcoded values. If it’s a match, then it calls `increment_transitions()` and updates `g_state` to a new ID via `set_next_state()`. If it fails: It calls `show_error_message()`, which acts as a dead end. 

To break it down in lament terms: 

In `state_16`, if the current character is 'k', we move to state 9878. But if it is 'J', we move to 28202. 

Obviously, we’re not going to explore the 90,000+ states manually, instead we’ll utilize the Breath-First-Search to walk the graph for us. We can do this by crafting a script to achieve this. 

## Locating the State Machine 
The core of the binary is a massive array of function pointers named STATES located at 0x140A38020. Each pointer in this array leads to a unique function representing a specific "state" in the machine. 

- Extraction: Read 96,767 quadwords from this address to map every state ID to its corresponding memory address. 
- Initialization: The program determines the starting state by XORing the first character of the input with 0x1337 and taking the modulo of the total number of states. 

```python
import idc 
import idaapi 
import idautils 
from collections import deque

STATES_ARRAY_ADDR = 0x140A38020  
NUM_STATES = 96767 

def calculate_entry_state(first_char): 
    return (0x1337 ^ ord(first_char)) 

def extract_function_pointers(): 
    print(f"[*] Reading {NUM_STATES} function pointers from 0x{STATES_ARRAY_ADDR:x}") 
    addrs = [] 
    ea = STATES_ARRAY_ADDR

    for i in range(NUM_STATES): 
        if i % 10000 == 0: 
            print(f"    Progress: {i}/{NUM_STATES}") 
        ptr = idc.get_qword(ea) 
        if ptr == 0 or ptr == idc.BADADDR: 
            print(f"[!] Invalid pointer at index {i}") 
            addrs.append(None) 
        else: 
            addrs.append(ptr) 
        ea += 8 

    valid = sum(1 for a in addrs if a is not None) 
    print(f"[+] Extracted {valid}/{NUM_STATES} valid pointers") 
    return addrs
```

## Dissecting the state transitions 

Every state function follows a rigid pattern to decide which state comes next based on out input. Instead of reading these manually, we use the script to parse the assembly instructions: 

- Input Comparison: The script scans for cmp instructions where the input character (stored in al or cl) is checked against a hardcoded value. 
- Success Branching: It follows the jz (jump if zero) or je (jump if equal) instructions to find the logic that executes when a character match is found. 
- Destination Extraction: It looks for a mov instruction (typically into registers like ecx or rdi) that sets the next state ID, allowing us to draw an "edge" between states. 

```python
def parse_small_state(func_ea, state_id): 
    if not idc.get_func_attr(func_ea, idc.FUNCATTR_START): 
        idc.create_function(func_ea) 
        idc.set_name(func_ea, f"state_{state_id}", idc.SN_NOWARN) 

    transitions = {} 
    func_start = idc.get_func_attr(func_ea, idc.FUNCATTR_START) 
    func_end = idc.get_func_attr(func_ea, idc.FUNCATTR_END) 

    if not func_end or func_end == idc.BADADDR: 
        func_end = func_start + 0x200 

    ea = func_start 
    max_ins = 100 
    count = 0 
    current_char = None 

    while ea < func_end and count < max_ins: 
        mnem = idc.print_insn_mnem(ea) 
        if mnem == "cmp": 
            op1 = idc.print_operand(ea, 0) 

            if op1 in ["al", "cl"]: 
                char_val = idc.get_operand_value(ea, 1) 

                if 0x20 <= char_val <= 0x7E: 
                    current_char = chr(char_val) 

        elif mnem in ["jz", "je"] and current_char: 
            target = idc.get_operand_value(ea, 0) 

            scan_ea = target 
            for _ in range(10): 
                if scan_ea >= func_end: 
                    break 
                 
                scan_mnem = idc.print_insn_mnem(scan_ea) 
                if scan_mnem == "mov": 
                    scan_op1 = idc.print_operand(scan_ea, 0) 
                    if scan_op1 in ["ecx", "rcx", "edi", "rdi"]: 
                        next_state = idc.get_operand_value(scan_ea, 1)

                        if 0 <= next_state < NUM_STATES: 
                            transitions[current_char] = next_state 
                            current_char = None 
                            break 

                scan_ea = idc.next_head(scan_ea) 

        elif mnem == "call": 
            target_name = idc.get_func_name(idc.get_operand_value(ea, 0)) 
            if target_name == "get_current_input": 
                current_char = None 

        ea = idc.next_head(ea) 
        count += 1 

    return transitions
```

Breath-First-Search Solve 

Because the state space is too large for a simple brute-force approach, we use a Breadth-First Search (BFS) to "walk" the graph efficiently. 

Since the first character of the password determines the starting state and acts as the first transition, we iterate through all printable characters (A-Z, 0-9): 
- Calculate the `entry_state` for a character. 
- Parse that `entry_state` function to see if it accepts that same character. 
- If it does, we have a valid starting point for our search. 

```python
def parse_state_function(func_ea, state_id): 
    if not func_ea: 
        return {}

    if not idc.get_func_attr(func_ea, idc.FUNCATTR_START): 
        idc.create_function(func_ea) 
        idc.set_name(func_ea, f"state_{state_id}", idc.SN_NOWARN) 

    transitions = {} 
    func_start = idc.get_func_attr(func_ea, idc.FUNCATTR_START) 
    func_end = idc.get_func_attr(func_ea, idc.FUNCATTR_END) 

    if not func_end or func_end == idc.BADADDR: 
        func_end = func_start + 0x500 

    ea = func_start 
    max_instructions = 200 
    count = 0 
    while ea < func_end and ea != idc.BADADDR and count < max_instructions: 
        mnem = idc.print_insn_mnem(ea) 

        # Pattern: cmp al, <char> 
        if mnem == "cmp": 
            op0 = idc.print_operand(ea, 0)

            if op0 == "al":  # Character comparison 
                char_val = idc.get_operand_value(ea, 1) 

                if 0x20 <= char_val <= 0x7E:  # Printable ASCII 
                    char = chr(char_val) 
                    next_ea = idc.next_head(ea) 
                    next_mnem = idc.print_insn_mnem(next_ea)

                    if next_mnem in ["jz", "je"]: 
                        target = idc.get_operand_value(next_ea, 0) 
                        # Scan target block for: mov ecx, <state>; jmp set_next_state 
                        next_state = scan_for_mov_ecx(target, func_end) 

                        if next_state is not None and 0 <= next_state < NUM_STATES: 
                            transitions[char] = next_state

        ea = idc.next_head(ea) 
        count += 1

    return transitions 

def scan_for_mov_ecx(start_ea, end_ea, max_scan=20): 
    ea = start_ea 
    count = 0 
    while ea < end_ea and ea != idc.BADADDR and count < max_scan: 
        mnem = idc.print_insn_mnem(ea) 

        # Look for: mov ecx, <value> 
        if mnem == "mov": 
            dest = idc.print_operand(ea, 0) 
            if dest in ["ecx", "rcx"]: 
                next_state = idc.get_operand_value(ea, 1) 

                # Verify next instruction is jmp set_next_state 
                next_ea = idc.next_head(ea) 
                while idc.print_insn_mnem(next_ea) == "add":  # Skip stack cleanup 
                    next_ea = idc.next_head(next_ea) 

                if idc.print_insn_mnem(next_ea) == "jmp": 
                    target = idc.get_operand_value(next_ea, 0) 
                    target_name = idc.get_func_name(target) 

                    if target_name == "set_next_state": 
                        return next_state

        ea = idc.next_head(ea) 
        count += 1 

    return None 

def scan_for_next_state(start_ea, end_ea, max_scan=30): 
    ea = start_ea 
    count = 0 

    while ea < end_ea and ea != idc.BADADDR and count < max_scan: 
        mnem = idc.print_insn_mnem(ea) 

        if mnem == "call": 
            target = idc.get_operand_value(ea, 0) 
            func_name = idc.get_func_name(target) 

            if func_name == "set_next_state": 
                arg_ea = idc.prev_head(ea) 

                for _ in range(10): 
                    if idc.print_insn_mnem(arg_ea) == "mov": 
                        dest = idc.print_operand(arg_ea, 0) 

                        # Windows x64: RCX, Linux x64: RDI 
                        if dest in ["ecx", "rcx", "edi", "rdi"]: 
                            return idc.get_operand_value(arg_ea, 1)
 
                    arg_ea = idc.prev_head(arg_ea) 

        # Alternative: direct mov to state variable 
        elif mnem == "mov": 
            dest = idc.print_operand(ea, 0) 

            # Look for writes to g_state variable (memory location) 
            if "qword ptr" in dest or "dword ptr" in dest: 
                return idc.get_operand_value(ea, 1) 

        ea = idc.next_head(ea) 
        count += 1 

    return None
```

Graph Traversal and Pruning 

From a valid starting point, the BFS explores every possible valid transition layer-by-layer. 

Path Length: We only care about paths that reach exactly 16 characters. 

Visited States: To prevent the script from getting stuck in infinite loops, we track (state_id, path_length). If we reach the same state at the same character position again, we discard that branch. 
```python
def solve(graph): 
    print("[*] Solving for 16-character password...") 
    import string

    solutions = [] 
    for fc in string.ascii_letters + string.digits: 
        entry = calculate_entry_state(fc) 
        if entry not in graph: 
            continue 

        if fc not in graph[entry]: 
            continue 

        print(f"[*] Trying '{fc}' (entry={entry})...", end='\r') 
        first_next = graph[entry][fc] 
        q = deque([(first_next, fc)]) 
        vis = set() 

        while q and len(solutions) < 10: 
            s, p = q.popleft() 
            if (s, len(p)) in vis: 
                continue 
            vis.add((s, len(p))) 

            if len(p) == 16: 
                print(f"\n[+] SOLUTION: {p}") 
                solutions.append(p) 
                break 

            if s in graph: 
                for c, n in graph[s].items(): 
                    if len(p) < 16: 
                        q.append((n, p + c)) 
    return solutions
```

Running this script in IDA > File > Script file, we’re able to find the exact password that the program uses (might take a minute or two): 

<img width="522" height="295" alt="image" src="https://github.com/user-attachments/assets/d4499692-ff37-49d0-8fb2-a72c848bd03a" />

Inputting this password into the program, we get the flag: 

<img width="322" height="48" alt="image" src="https://github.com/user-attachments/assets/9b92315d-fa72-4362-9cf1-5526f213159b" />

`Flag = flag{FSM_1s_S0_c0000l} `
