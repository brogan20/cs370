# Dominick DiMaggio, Brogan Clements, Ishaan Patel
# I pledge my honor that I have abided by the Stevens Honor System.

def lcs_dp_rec(s1, s2):
    # Initialize DP matrix with 0 values
    arr = [[0] * (len(s2)+1) for _ in range(len(s1)+1)]

    # Set up DP matrix
    for i in range(1, len(s1)+1):
        for j in range(1, len(s2)+1):
            if(s1[i-1] == s2[j-1]):
                arr[i][j] = arr[i-1][j-1] + 1
            else:
                arr[i][j] = max(arr[i][j-1], arr[i-1][j])

    # just some variable initalization, meh
    subs = set()
    states = set()
    buf = ''
    i = len(s1)
    j = len(s2)
    
    # recursive helper method
    def lcs_helper(i, j, buf):
        cur_state = (i, j, buf)

        # prevents duplicate calculations
        # basically memoization
        if cur_state not in states:
            states.add(cur_state)
        else:
            return

        if i > 0 and j > 0:
            # if both cities are the same, use it
            if s1[i-1] == s2[j-1]:
                buf = s1[i-1] + buf
                lcs_helper(i-1, j-1, buf)
            # all of these are basically lose it variants
            elif arr[i][j-1] == arr[i-1][j]:
                lcs_helper(i, j-1, buf)
                lcs_helper(i-1, j, buf)
            elif arr[i][j-1] > arr[i-1][j]:
                lcs_helper(i, j-1, buf)
            else:
               lcs_helper(i-1, j, buf)
        else: # add sequence to list
            subs.add(buf)

    lcs_helper(i, j, buf)

    # prints out all sequences, sorted
    lst = list(subs)
    lst.sort()
    for s in lst:
        print(s)

# gets the amount of test cases
iters = int(input())

if iters < 1:
    exit()

# calls the lcs method for each test case
for _ in range(iters-1):
    lcs_dp_rec(input(), input())
    print()
lcs_dp_rec(input(), input())