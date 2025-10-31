#include <iostream>
using namespace std;
int main(int argc, char** argv)
{
  for (int x = 0 ; x < 1000 ; x++)
  {
    for (int y = 0 ; y < 1000 ; y++)
    {
      if (7*x*x + 2*y*y < x*y)
      {
        cout << x << " " << y << endl;
      }
    }
  }
  return 0;
}
