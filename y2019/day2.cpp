#include <iostream>
#include <cstring>

using namespace std;


int execute(int prog[], int progLength)
{
  int ptr = 0;
  while (ptr < progLength)
  {
    switch (prog[ptr])
    {
      case 1:
        prog[prog[ptr+3]] = prog[prog[ptr+1]] + prog[prog[ptr+2]];
        ptr += 4;
        break;
      case 2:
        prog[prog[ptr+3]] = prog[prog[ptr+1]] * prog[prog[ptr+2]];
        ptr += 4;
        break;
      case 99:
        return prog[0];
      default:
        throw "wtf";
    }
  }
  return prog[0];
}

int main(int argc, char** argv)
{
  int prog[150];
  int line = 0;
  int lineNumber = 0;
  while (cin >> line)
  {
    prog[lineNumber++] = line;
  }
  int noun = 0;
  int verb = 0;
  for ( ; noun < 100 ; noun++)
  {
    for ( ; verb < 100 ; verb++)
    {
      int progToRun[150];
      memcpy(progToRun, prog, 150*sizeof(int));
      progToRun[1] = noun;
      progToRun[2] = verb;
      int result = execute(progToRun, 150);
      cout << "Noun: " << noun << ", Verb: " << verb << " result: " << result << endl;
      if (result == 19690720)
      {
        cout << 100*noun + verb;
        return 0;
      }
    }
    verb = 0;
  }
  return 0;
}
