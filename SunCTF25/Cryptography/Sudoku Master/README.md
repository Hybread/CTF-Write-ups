# Sudoku Master

<img width="598" height="364" alt="image" src="https://github.com/user-attachments/assets/83cd34c0-96f4-4b98-860b-9a92b610610b" />

## Solve
Downloaded the two provided files, and inspect the contents within.

From the contents of chall.py, we can understand that it's a script that generates a sudoku-like puzzle which includes a permutation matrix, unimodular matrix, with a bit of added noise in the output.
Meanwhile, in the public.json file, the content appear to be Sudoku puzzle, defined by the variable puzzle_rows, a compact puzzle string, a unimodular matrix U, a matrix A, 
and a noisy vector y.

```python
def generate_puzzle(blanks=45, ensure_unique=True, max_attempts=10000):
    # generate full board, then remove blanks while keeping uniqueness if desired
    board = [[0]*SIZE for _ in range(SIZE)]
    fill_board(board)
    solution = deepcopy(board)
    # remove cells
    cells = [(i,j) for i in range(SIZE) for j in range(SIZE)]
    random.shuffle(cells)
    removed = 0
    attempts = 0
    while removed < blanks and cells and attempts < max_attempts:
        attempts += 1
        i,j = cells.pop()
        saved = board[i][j]
        board[i][j] = 0
        if ensure_unique:
            b2 = deepcopy(board)
            if solve_count(b2, 2) != 1:
                board[i][j] = saved
                continue
        removed += 1
    return board, solution
```
Based on this function, we see that it is used to generate a 9x9 grid and 3x3 subgrid sudoku board layout, with value placement checking, empty cell locating with variable 'find_empty',
'fill_board' to fill and generate a full sudoku board and 'solve_count' to check for unique values like you would in a sudoku game.

---
Going through the rest of the matrix generation functions, we find that the flag is encoded in a linear system defined by y = A m + noise, where 'm' is an 81-byte vector 
representing the flag that's padded with 0's, 'A' being a matrix composed as 'A = U P D', and noise consists of small random integers in the range [-1, 1]. 

The matrix A is constructed from three components: 
- 'U' being a unimodular lower-triangular matrix with 1s on the diagonal
- 'P' being a permutation matrix derived from the Sudoku solution
- Finally, 'D' which is a diagonal matrix with prime numbers corresponding to Sudoku digits (1 maps to 55633, 2 to 36529, and so on, up to 9 mapping to 47251)

Understanding the entire code, we must recover 'm', then decode it as a string and we get the flag.
To do this we'll come up with a script in which we reverse each key functions from the source chall.py.
```python
import json

SIZE, BOX = 9, 3
PRIMES = [55633, 36529, 50543, 60703, 41539, 61403, 65497, 44129, 47251]  # digit 1..9

def valid(board, r, c, v):
    if any(board[r][j] == v for j in range(SIZE)): return False
    if any(board[i][c] == v for i in range(SIZE)): return False
    br, bc = (r//BOX)*BOX, (c//BOX)*BOX
    for i in range(br, br+BOX):
        for j in range(bc, bc+BOX):
            if board[i][j] == v: return False
    return True

def empty(board):
    for i in range(SIZE):
        for j in range(SIZE):
            if board[i][j] == 0:
                return i, j
    return None, None

def solve(board):
    r, c = empty(board)
    if r is None:
        return True
    for v in range(1, SIZE+1):
        if valid(board, r, c, v):
            board[r][c] = v
            if solve(board): return True
            board[r][c] = 0
    return False

def permutation(sol):
    cells = [(r,c) for r in range(9) for c in range(9)]
    canon = {(r,c): r*9 + c for (r,c) in cells}
    sorted_cells = sorted(cells, key=lambda rc: (sol[rc[0]][rc[1]], rc[0], rc[1]))
    pi = [0]*81
    for s_idx, rc in enumerate(sorted_cells):
        pi[canon[rc]] = s_idx
    return pi 

def unitLowerTriangular(U, b):
    n = len(U)
    z = [0]*n
    for i in range(n):
        s = b[i]
        row = U[i]
        for j in range(i):
            s -= row[j]*z[j]
        z[i] = s
    return z

def main():
    with open("public.json","r") as f:
        pub = json.load(f)
    U = pub["U"]
    y = pub["y"]
    puzzle = [row[:] for row in pub["puzzle_rows"]]

    if not solve(puzzle):
        raise RuntimeError("Sudoku unsolved (unexpected)")

    pi = permutation(puzzle)            
    inv_pi = [0]*81
    for c_idx, s_idx in enumerate(pi):
        inv_pi[s_idx] = c_idx                   

    d = []
    for r in range(9):
        for c in range(9):
            d.append(PRIMES[puzzle[r][c]-1])

    z = unitLowerTriangular(U, y)

    m = [0]*81
    for s_idx in range(81):
        c_idx = inv_pi[s_idx]
        byte = int(round(z[s_idx] / d[c_idx]))
        if byte < 0: byte = 0
        if byte > 255: byte = 255
        m[c_idx] = byte

    b = bytes(m)
    txt = b.decode("latin1", errors="ignore")
    end = txt.rfind("}")
    if end != -1:
        flag = txt[:end+1]
    else:
        while m and m[-1] == 0: m.pop()
        flag = bytes(m).decode("latin1", errors="ignore")

    print("Solved Sudoku:")
    for r in range(9):
        row = puzzle[r]
        print(" ".join(str(x) for x in row))

    print("\nFlag:")
    print(flag)

if __name__ == "__main__":
    main()
```
The script contains the reversed functions of the source chall.py. It takes the contents of the public.json file and computes the sudoku board solve based on it. Running the script
returns the decoded flag:
```
Solved Sudoku:
5 6 2 7 9 3 4 1 8
8 1 4 5 2 6 9 7 3
3 9 7 8 4 1 5 6 2
1 7 3 2 8 4 6 9 5
6 2 9 3 1 5 8 4 7
4 8 5 9 6 7 3 2 1
2 5 6 4 7 8 1 3 9
7 3 1 6 5 9 2 8 4
9 4 8 1 3 2 7 5 6

Flag:
sunctf25{Sud0ku_Latt1ce_Perm_U_D_tr4pd0or!}
```
