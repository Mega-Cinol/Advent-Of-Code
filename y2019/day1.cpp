#include <iostream>

using namespace std;

int fuel(int mass)
{
  int sum = 0;
  while (mass > 0)
  {
    cout << mass << endl;
    mass = mass / 3 - 2;
    mass = mass < 0 ? 0 : mass;
    sum += mass;
  }
  return sum;
}

int main(int argc, char** argv)
{
  int sum = 0;
  int line = 0;
  while (cin >> line)
  {
    int toAdd = line /3 -2;
    sum += toAdd;
    sum += fuel(toAdd);
  }
  cout << sum << endl;
  cout << 33583 + fuel(33583) << endl;
  return 0;
}
