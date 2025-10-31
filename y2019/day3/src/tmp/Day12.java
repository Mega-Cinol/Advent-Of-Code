package tmp;

import java.util.ArrayList;
import java.util.List;

public class Day12
{
    private static class Planet
    {
        private long x;
        private long y;
        private long z;

        private long vx = 0;
        private long vy = 0;
        private long vz = 0;

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (vx ^ (vx >>> 32));
            result = prime * result + (int) (vy ^ (vy >>> 32));
            result = prime * result + (int) (vz ^ (vz >>> 32));
            result = prime * result + (int) (x ^ (x >>> 32));
            result = prime * result + (int) (y ^ (y >>> 32));
            result = prime * result + (int) (z ^ (z >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Planet other = (Planet) obj;
            if (vx != other.vx) return false;
            if (vy != other.vy) return false;
            if (vz != other.vz) return false;
            if (x != other.x) return false;
            if (y != other.y) return false;
            if (z != other.z) return false;
            return true;
        }

        public Planet(long x, long y, long z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void adjustVelocity(List<Planet> planets)
        {
            planets.forEach(this::adjustVelocity);
        }
        public void adjustVelocity(Planet other)
        {
//            if (x > other.x)
//            {
//                vx--;
//            }
//            else if (x < other.x)
//            {
//                vx++;
//            }
//            if (y > other.y)
//            {
//                vy--;
//            }
//            else if (y < other.y)
//            {
//                vy++;
//            }
            if (z > other.z)
            {
                vz--;
            }
            else if (z < other.z)
            {
                vz++;
            }
        }
        public void move()
        {
            x += vx;
            y += vy;
            z += vz;
        }
        @Override
        public String toString()
        {
            return "pos=< x = " + x + ", y = " + y + ", z = " + z + " >, vel=< x = " + vx + ", y = " + vy + ", z = " + vz + " >";
        }
        public long pot()
        {
            return Math.abs(x) + Math.abs(y) + Math.abs(z);
        }
        public long kin()
        {
            return Math.abs(vx) + Math.abs(vy) + Math.abs(vz);
        }
        public long energy()
        {
            System.out.println(kin());
            System.out.println(pot());
            return kin() * pot();
        }
    }
    private static class PlanetSystem
    {
        private final List<Planet> planets = new ArrayList<>();
        public void addPlanet(Planet planet)
        {
            planets.add(planet);
        }
        public boolean hasAll(List<Planet> otherPlanets)
        {
            return planets.containsAll(otherPlanets);
        }
        public void step()
        {
            planets.forEach(planet -> planet.adjustVelocity(planets));
            planets.forEach(Planet::move);
        }
        public void print()
        {
            planets.forEach(System.out::println);
        }
        public long energy()
        {
            planets.stream().map(Planet::energy).forEach(System.out::println);
            return planets.stream().reduce(0L, (e, p) -> e + p.energy(), (e1, e2) -> e1 + e2); 
        }
    }
    private static long maxFactor(long num1, long num2)
    {
        num1 = Math.abs(num1);
        num2 = Math.abs(num2);
        while (num1 > 0 && num2 > 0)
        {
            if (num1 > num2)
            {
                num1 %= num2;
            }
            else
            {
                num2 %= num1;
            }
            System.out.println(num1 + ", " + num2);
        }
        return Math.max(num1, num2);
    }
    private static long combine(long num1, long num2)
    {
        long factor = maxFactor(num1, num2);
        System.out.println(num1 + " " + num2 + " " + factor);
        return num1/factor * num2;
    }
    public static void main(String[] args)
    {
//        PlanetSystem planetSystem = new PlanetSystem();
//        Planet p1 = new Planet(1, 3, -11);
//        Planet p2 = new Planet(17, -10, -8);
//        Planet p3 = new Planet(-1, -15, 2);
//        Planet p4 = new Planet(12, -4, -4);
//        planetSystem.addPlanet(p1);
//        planetSystem.addPlanet(p2);
//        planetSystem.addPlanet(p3);
//        planetSystem.addPlanet(p4);
//        p1 = new Planet(1, 3, -11);
//        p2 = new Planet(17, -10, -8);
//        p3 = new Planet(-1, -15, 2);
//        p4 = new Planet(12, -4, -4);
//        List<Planet> initialState = new ArrayList<>();
//        initialState.add(p1);
//        initialState.add(p2);
//        initialState.add(p3);
//        initialState.add(p4);
//        long step = 1;
//        planetSystem.step();
//        while (!planetSystem.hasAll(initialState))
//        {
//            planetSystem.step();
//            step++;
//        }
//        System.out.println(step);
//        for (long step = 0 ; step < 1000 ; step++)
//        {
//            System.out.println("Step " + step + ":");
//            planetSystem.step();
//            planetSystem.print();
//        }
//        System.out.println(planetSystem.energy());
        long a = 186028;
        long b = 268296;
        long c = 102356;
        long ab = combine(a,b);
//        int ab = b;
        long abc = combine(c, ab);
        System.out.println(abc);
    }
}
