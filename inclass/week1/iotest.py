# Ishaan is super cool!
# Dom is also very cool
# Maybe Brogan i dunno
# Brogan's okay I guess





line1 = input()
line1.split()
lines = int(line1[0])
divisor = int(line1[2])

count = 0
for _ in range(lines):
    if int(input()) % divisor == 0:
        count += 1
print(count)