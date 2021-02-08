CXX      = g++
CXX_FILE = $(wildcard *.cpp)
TARGET   = $(patsubst %.cpp,%,$(CXX_FILE))
CXXFLAGS = -g -fmessage-length=0

all:
	$(CXX) $(CXXFLAGS) $(CXX_FILE) -o $(TARGET) -pthread
clean:
	rm -f $(TARGET) $(TARGET).exe