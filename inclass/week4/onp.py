numExp = int(input())

operators = ["+", "-", "*", "/", "^"]
stack = []

for i in range(numExp):
    exp = input()
    for j in range(len(exp)):
        if(exp[j].isalpha()):
            print(exp[j], end='')
            continue
        elif(exp[j] in operators):
            if len(stack) != 0:
                while stack[len(stack) - 1] in operators and operators.index(len(stack) - 1) <= operators.index(exp[j]):
                    output = stack.pop()
                    print(output, end="")
            stack.append(exp[j])
            continue
        elif(exp[j] == "("):
            stack.append("(")
        elif(exp[j] == ")"):
            while(stack[len(stack) - 1] != "("):
                output = stack.pop()
                print(output, end="")
            stack.pop()
    while(len(stack) != 0):
        output = stack.pop()
        print(output, end="")
    print()
