import sys

paths = []
target = None

def Remove(lst):
     return ([list(i) for i in {*[tuple(sorted(i)) for i in lst]}])  

def vimwar(lst, n, sum, path):
    global paths
    global target
 
    # Base Cases
    if (n == 0):
        if sum == target:
          paths += [path]
        #print("Domp")
        return False
    if (sum == target):
        #print("Womp")
        paths += [path]
        #vimwar(lst, n-1, sum, path)
        #return False

 
    # If last element is greater than
    # sum, then ignore it
    #if (lst[n - 1] > sum):
    #    return vimwar(lst, n - 1, sum, path)
 
    # else, check if sum can be obtained
    # by any of the following
    # (a) excluding the last element
    # (b) including the last element

    vimwar(lst, n-1, sum, path)
    vimwar(lst, n-1, sum | lst[n-1], path + [lst[n-1]])


sys.setrecursionlimit(9999)
# Driver code
tmp = input().split()
lines = int(tmp[0])
numskills = int(tmp[1])
nums = []

for _ in range(lines):
    nums += [int(input(), 2)]
target = int(input(), 2)

#print(nums)

lst = [3, 34, 4, 12, 5, 2]
sum = 9
n = len(lst)

#isSublstSum(lst, n, sum, [])
#print(target)
vimwar(nums, len(nums), 0, [])

paths = Remove(paths)
#print(paths)
print(len(paths))