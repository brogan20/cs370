class Stack(object):    
    def __init__(self):        
        self.items = []    
        
    def is_empty(self):        
        return self.items == []    
    
    def push(self, item):        
        self.items.append(item)    
        
    def pop(self):
        return self.items.pop()    
    
    def peek(self):        
        return self.items[len(self.items) - 1]    
    
    def size(self):        
        return len(self.items)
    
class Queue(object):    
    def __init__(self):        
        self.stack1 = Stack()        
        self.stack2 = Stack()    
        
    def enqueue(self, item):        
        self.stack1.push(item)          
    
    def dequeue(self):        
        if self.stack2.is_empty():
            while not self.stack1.is_empty():
                self.stack2.push(self.stack1.pop())
            
        self.stack2.pop()

    
    def front(self):        
        if self.stack2.is_empty():
            while not self.stack1.is_empty():
                self.stack2.push(self.stack1.pop())
            
        return self.stack2.peek()
            
if __name__ == '__main__':    
    q = Queue()
    lines = int(input())
    
    for _ in range(lines):
        line = input().split(" ")
        cmd = int(line[0])
        
        if cmd == 1:
            q.enqueue(int(line[1]))
        elif cmd == 2:
            q.dequeue()
        elif cmd == 3:
            print(q.front())    
        
    
    
    
    
    
