package tmp;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tmp.IntComputer.InputProvider;
import tmp.IntComputer.OutputConsumer;

public class Day23
{
    private static class Packet
    {
        public long x = 0;
        public long y = 0;
    }

    private static class Cluster
    {
        private Map<Integer, Nic> nodes = new HashMap<>();
        private Set<Integer> idleNodes = new HashSet<>();
        private Packet natPacket = null;
        private Long lastY = null; 

        public void sendToNode(int receiver, Packet packet)
        {
            if (receiver == 255)
            {
                System.out.println(receiver);
                System.out.println(packet.x);
                System.out.println(packet.y);
                natPacket = packet;
            }
            else
            {
                nodes.get(receiver).addPacket(packet);
            }
        }

        public synchronized void reportIdle(int nodeId)
        {
            idleNodes.add(nodeId);
            if (idleNodes.size() == 50)
            {
                if (lastY != null && lastY == natPacket.y)
                {
                    System.out.println("+++");
                    System.out.println("Result: " + lastY);
                    System.out.println("+++");
                }
                lastY = natPacket.y;
                sendToNode(0, natPacket);
            }
        }

        public synchronized void reportBusy(int nodeId)
        {
            idleNodes.remove(nodeId);
        }

        public Cluster(List<BigDecimal> program, int size)
        {
            for (int i = 0; i < size; i++)
            {
                Nic nic = new Nic(this, i);
                nodes.put(i, nic);
            }
            for (Nic nic : nodes.values())
            {
                new Thread(() -> {
                    nic.start(program);
                }).start();
            }
        }
    }

    private static class Nic implements InputProvider, OutputConsumer
    {
        private Deque<Packet> packets = new ArrayDeque<>();
        private Long pendingY = null;

        int state = 0;
        int receiver = 0;
        long xToSend = 0;

        boolean initializing = true;
        int index = -1;
        int idleCount = 0;

        private final Cluster cluster;

        public Nic(Cluster cluster, int index)
        {
            this.cluster = cluster;
            this.index = index;
        }

        public void start(List<BigDecimal> program)
        {
            IntComputer computer = new IntComputer();
            computer.executeProgram(program, this, this);
        }

        @Override
        public synchronized void consumeOutput(String output)
        {
            switch (state)
            {
                case 0:
                    receiver = Integer.parseInt(output);
                    break;
                case 1:
                    xToSend = Long.parseLong(output);
                    break;
                case 2:
                    Packet packet = new Packet();
                    packet.x = xToSend;
                    packet.y = Long.parseLong(output);
                    System.out.println(String.format("%d: Sending packet: [%d, %d] to %d", index, packet.x, packet.y, receiver));
                    cluster.sendToNode(receiver, packet);
                    break;
                default:
                    break;
            }
            state++;
            state %= 3;
            idleCount = 0;
            cluster.reportBusy(index);
        }

        public synchronized void addPacket(Packet packet)
        {
            packets.addLast(packet);
            idleCount = 0;
            cluster.reportBusy(index);
        }

        @Override
        public synchronized long generateInput()
        {
            try
            {
                wait(10);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            if (initializing)
            {
                initializing = false;
                return index;
            }
            if (pendingY != null)
            {
                long tmp = pendingY;
                pendingY = null;
                System.out.println(String.format("%d: Outputing packet [x, %d], outputing y", index, tmp));
                return tmp;
            }
            if (packets.isEmpty())
            {
                idleCount++;
                if (idleCount > 10)
                {
                    cluster.reportIdle(index);
                }
                return -1;
            }
            Packet packet = packets.removeFirst();
            pendingY = packet.y;
            System.out.println(String.format("%d: Outputing packet [%d, %d], outputing x", index, packet.x, pendingY));
            return packet.x;
        }
    }

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        List<BigDecimal> program = Stream.of(scanner.next().split(",")).map(BigDecimal::new).collect(Collectors.toList());
        scanner.close();
        new Cluster(program, 50);
    }

}
