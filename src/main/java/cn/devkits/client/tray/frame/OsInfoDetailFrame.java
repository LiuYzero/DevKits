/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.SpringLayout.Constraints;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import cn.devkits.client.util.DKSystemUIUtil;
import cn.devkits.client.util.DKSystemUtil;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.hardware.SoundCard;
import oshi.hardware.UsbDevice;
import oshi.hardware.VirtualMemory;
import oshi.hardware.platform.windows.WindowsUsbDevice;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

/**
 * 当前计算机软硬件信息展示
 *
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年10月21日 下午8:23:09
 */
public class OsInfoDetailFrame extends DKAbstractFrame {

    private static final long serialVersionUID = 6295111163819170866L;
    private JTabbedPane jTabbedPane;
    private SystemInfo si = new SystemInfo();

    public OsInfoDetailFrame() {
        super(DKSystemUIUtil.getLocaleString("SYS_INFO_TITLE"), 0.9f);

        initUI(getContentPane());
        initListener();
    }

    @Override
    protected void initUI(Container rootContainer) {
        JToolBar toolBar = new JToolBar("System Information Toolbar");
        toolBar.setFloatable(false);
        createToolbarBtns(toolBar);

        setPreferredSize(new Dimension(450, 130));
        add(toolBar, BorderLayout.PAGE_START);

        this.jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_DASHBOARD"), initDashboard(si.getHardware(), si.getOperatingSystem()));
        jTabbedPane.addTab("CPU", new JScrollPane());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_MAINBOARD"), new JScrollPane());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_MEMORY"), new JScrollPane());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_DISK"), new JScrollPane());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_SENSORS"), new JScrollPane());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_DISPLAYS"), new JScrollPane());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_NETWORK"), new JScrollPane());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_SOUNDCARDS"), new JScrollPane());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_USB_DEVICES"), new JScrollPane());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_POWER_SOURCES"), new JScrollPane());

        jTabbedPane.setFocusable(false);// 不显示选项卡上的焦点虚线边框

        rootContainer.add(jTabbedPane, BorderLayout.CENTER);
    }

    private void createToolbarBtns(JToolBar toolBar) {
        JButton systemBtn = new JButton(DKSystemUIUtil.getLocaleString("SYS_INFO_TOOL_BAR_SYS"));
        systemBtn.setFocusable(false);
        systemBtn.addActionListener(e -> {
            DKSystemUtil.openSystemInfoClient("msinfo32");
        });
        toolBar.add(systemBtn);

        JButton dxBtn = new JButton(DKSystemUIUtil.getLocaleString("SYS_INFO_TOOL_BAR_DX"));
        dxBtn.setFocusable(false);
        dxBtn.addActionListener(e -> {
            DKSystemUtil.openSystemInfoClient("dxdiag");
        });
        toolBar.add(dxBtn);
    }


    @Override
    protected void initListener() {
        jTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane pane = (JTabbedPane) e.getSource();
                int tabIndex = pane.getSelectedIndex();
                JScrollPane container = (JScrollPane) pane.getSelectedComponent();
                if (container.getViewport().getView() != null) {// 面板上已经有组件承载内容显示，无需刷新
                    return;
                }
                switch (tabIndex) {
                    case 1:
                        container.setViewportView(initCpu(si.getHardware()));
                        break;
                    case 2:
                        container.setViewportView(initMainboard());
                        break;
                    case 3:
                        container.setViewportView(initMemory(si.getHardware()));
                        break;
                    case 4:
                        container.setViewportView(initDisk(si.getHardware(), si.getOperatingSystem()));
                        break;
                    case 5:
                        container.setViewportView(initSensors(si.getHardware()));
                        break;
                    case 6:
                        container.setViewportView(initDisplay(si.getHardware()));
                        break;
                    case 7:
                        container.setViewportView(initNetwork(si.getHardware(), si.getOperatingSystem()));
                        break;
                    case 8:
                        container.setViewportView(initSoundCards(si.getHardware()));
                        break;
                    case 9:
                        container.setViewportView(initUsb(si.getHardware()));
                        break;
                    case 10:
                        container.setViewportView(initPower(si.getHardware()));
                        break;
                    default:
                        break;
                }
                // container.revalidate();
                // container.repaint();
            }
        });
    }

    private Component initNetwork(HardwareAbstractionLayer hal, OperatingSystem os) {
        NetworkIF[] networkIFs = hal.getNetworkIFs();

        StringBuilder sb = new StringBuilder("<html><body>Network Interfaces:<br>");

        if (networkIFs.length == 0) {
            sb.append(" Unknown");
        }
        for (NetworkIF net : networkIFs) {
            sb.append("<br> ").append(net.toString());
        }

        NetworkParams networkParams = os.getNetworkParams();

        sb.append("<br>Network parameters:<br> " + networkParams.toString());

        sb.append("</body></html>");

        return new JLabel(sb.toString());
    }

    private Component initCpu(HardwareAbstractionLayer hal) {
        CentralProcessor processor = hal.getProcessor();

        StringBuilder sb = new StringBuilder("<html><body>");

        sb.append("Context Switches/Interrupts: " + processor.getContextSwitches() + " / " + processor.getInterrupts()).append("<br>");

        long[] prevTicks = processor.getSystemCpuLoadTicks();
        long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
        sb.append("CPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks)).append("<br>");
        // Wait a second...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        sb.append("CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks)).append("<br>");
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

        sb.append(String.format("User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%", 100d * user / totalCpu, 100d * nice / totalCpu,
                100d * sys / totalCpu, 100d * idle / totalCpu, 100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu, 100d * steal / totalCpu)).append("<br>");
        sb.append(String.format("CPU load: %.1f%%", processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100)).append("<br>");
        double[] loadAverage = processor.getSystemLoadAverage(3);
        sb.append("CPU load averages:" + (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0])) + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2]))).append("<br>");
        // per core CPU
        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
        double[] load = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
        for (double avg : load) {
            procCpu.append(String.format(" %.1f%%", avg * 100));
        }
        sb.append(procCpu.toString()).append("<br>");
        long freq = processor.getProcessorIdentifier().getVendorFreq();
        if (freq > 0) {
            sb.append("Vendor Frequency: " + FormatUtil.formatHertz(freq)).append("<br>");
        }
        freq = processor.getMaxFreq();
        if (freq > 0) {
            sb.append("Max Frequency: " + FormatUtil.formatHertz(freq)).append("<br>");
        }
        long[] freqs = processor.getCurrentFreq();
        if (freqs[0] > 0) {
            StringBuilder sb1 = new StringBuilder("Current Frequencies: ");
            for (int i = 0; i < freqs.length; i++) {
                if (i > 0) {
                    sb1.append(", ");
                }
                sb1.append(FormatUtil.formatHertz(freqs[i]));
            }
            sb.append(sb1.toString()).append("<br>");
        }


        sb.append("<br><br>").append(processor.toString()).append("<br>");

        sb.append("</body></html>");

        return new JLabel(sb.toString());
    }

    private Component initMainboard() {
        return new JPanel();
    }

    private Component initMemory(HardwareAbstractionLayer hal) {
        GlobalMemory memory = hal.getMemory();

        StringBuilder sb = new StringBuilder("<html><body>Sensors: <br>");

        sb.append("Memory: <br> " + memory.toString());
        VirtualMemory vm = memory.getVirtualMemory();
        sb.append("Swap: <br> " + vm.toString());
        PhysicalMemory[] pmArray = memory.getPhysicalMemory();
        if (pmArray.length > 0) {
            sb.append("Physical Memory: ");
            for (PhysicalMemory pm : pmArray) {
                sb.append(" " + pm.toString());
            }
        }

        sb.append("</body></html>");

        return new JLabel(sb.toString());
    }

    private Component initSensors(HardwareAbstractionLayer hal) {
        Sensors sensors = hal.getSensors();

        StringBuilder sb = new StringBuilder("<html><body>Sensors: <br>");

        sb.append(sensors.toString()).append("<br>");

        sb.append("</body></html>");

        return new JLabel(sb.toString());
    }

    private Component initDisplay(HardwareAbstractionLayer hal) {
        Display[] displays = hal.getDisplays();

        StringBuilder sb = new StringBuilder("<html><body>Displays: <br>");

        int i = 0;
        for (Display display : displays) {
            sb.append(" Display " + i + ":").append("<br>");
            sb.append(String.valueOf(display));
            i++;
        }

        sb.append("</body></html>");

        return new JLabel(sb.toString());
    }

    private Component initUsb(HardwareAbstractionLayer hal) {
        UsbDevice[] usbDevices = hal.getUsbDevices(true);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("USB Devices");
        for (UsbDevice usbDevice : usbDevices) {
            appendNodes(root, usbDevice);
        }

        JTree jTree = new JTree(root);

        TreeNode rootNode = (TreeNode) jTree.getModel().getRoot();
        DKSystemUIUtil.expandAll(jTree, new TreePath(rootNode), true);

        return jTree;
    }

    private String appendVenderInfo(UsbDevice usbDevice) {
        if (!usbDevice.getVendor().trim().isEmpty()) {
            return " (" + usbDevice.getVendor() + ")";
        }
        return "";
    }

    private void appendNodes(DefaultMutableTreeNode root, UsbDevice usbDevice) {
        DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(usbDevice.getName() + appendVenderInfo(usbDevice));
        root.add(newChild);

        UsbDevice[] connectedDevices = usbDevice.getConnectedDevices();
        if (connectedDevices.length > 0) {
            for (UsbDevice dev : connectedDevices) {
                appendNodes(newChild, dev);
            }
        }
    }

    private Component initSoundCards(HardwareAbstractionLayer hal) {
        SoundCard[] soundCards = hal.getSoundCards();

        StringBuilder sb = new StringBuilder("<html><body>Sound Cards: <br>");

        for (SoundCard card : soundCards) {
            sb.append(" " + String.valueOf(card)).append("<br>");
        }
        sb.append("</body></html>");

        return new JLabel(sb.toString());
    }

    private Component initPower(HardwareAbstractionLayer hal) {
        PowerSource[] powerSources = hal.getPowerSources();

        StringBuilder sb = new StringBuilder("<html><body>Power Sources: <br>");
        if (powerSources.length == 0) {
            sb.append("Unknown<br>");
        }
        for (PowerSource powerSource : powerSources) {
            sb.append("<br> ").append(powerSource.toString());
            sb.append("<br>");
        }
        sb.append("</body></html>");

        return new JLabel(sb.toString());
    }

    private Component initDisk(HardwareAbstractionLayer hal, OperatingSystem os) {
        Box vBox = Box.createVerticalBox();

        JTable partitionTable = new JTable(new DiskTableModel(hal.getDiskStores())) {
            /** serialVersionUID */
            private static final long serialVersionUID = -4006029161807662936L;

            @Override
            public Dimension getPreferredScrollableViewportSize() {
                return DKSystemUIUtil.updatePreferredScrollableViewportSize(this);
            }
        };
        DKSystemUIUtil.fitTableColumns(partitionTable);
        JScrollPane diskScrollPanel = new JScrollPane();
        diskScrollPanel.setViewportView(partitionTable);
        diskScrollPanel.setBorder(BorderFactory.createTitledBorder("Physical Disks"));
        vBox.add(diskScrollPanel);

        JTable fileSystemTable = new JTable(new FileSystemModel(os.getFileSystem().getFileStores())) {

            /** serialVersionUID */
            private static final long serialVersionUID = 7830630683277320571L;

            @Override
            public Dimension getPreferredScrollableViewportSize() {
                return DKSystemUIUtil.updatePreferredScrollableViewportSize(this);
            }
        };
        DKSystemUIUtil.fitTableColumns(fileSystemTable);
        JScrollPane fileScrollPane = new JScrollPane();
        fileScrollPane.setViewportView(fileSystemTable);
        fileScrollPane.setBorder(BorderFactory.createTitledBorder("File System:"));
        vBox.add(fileScrollPane);

        return vBox;
    }


    private JComponent initDashboard(HardwareAbstractionLayer hal, OperatingSystem os) {
        StringBuilder sb = new StringBuilder("<html><body>");
        sb.append(String.valueOf(os));
        sb.append("<br><br>");

        sb.append("Booted: " + Instant.ofEpochSecond(os.getSystemBootTime()));
        sb.append("<br>");
        sb.append("Uptime: " + FormatUtil.formatElapsedSecs(os.getSystemUptime()));
        sb.append("<br><br>");

        ComputerSystem computerSystem = hal.getComputerSystem();
        computerSystem.getManufacturer();

        sb.append("system: " + computerSystem.toString());
        sb.append("<br>");
        sb.append("firmware: " + computerSystem.getFirmware().toString());
        sb.append("<br>");
        sb.append("baseboard: " + computerSystem.getBaseboard().toString());
        sb.append("<br>");

        sb.append("OS Family: " + os.getFamily().intern());
        sb.append("<br>");
        sb.append("Bitness : " + os.getBitness());
        sb.append("<br>");
        sb.append("Manufacturer : " + os.getManufacturer());
        sb.append("<br>");
        sb.append("System Version : " + os.getVersion());
        sb.append("<br>");
        sb.append("Process Running: " + os.getProcessCount());
        sb.append("<br>");
        sb.append("Threads  Running: " + os.getThreadCount());
        sb.append("<br><br>");

        sb.append("Running with" + (os.isElevated() ? "" : "out") + " elevated permissions.");
        sb.append("</body></html>");

        JPanel jPanel = new JPanel(new GridLayout());
        jPanel.add(new JLabel(sb.toString()));

        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jPanel);
        return jScrollPane;
    }
}


class FileSystemModel implements TableModel {
    private OSFileStore[] fileStores;

    public FileSystemModel(OSFileStore[] fileStores) {
        this.fileStores = fileStores;
    }

    @Override
    public int getRowCount() {
        return fileStores.length;
    }

    @Override
    public int getColumnCount() {
        return 12;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Name";
            case 1:
                return "Volume";
            case 2:
                return "Logical Volume";
            case 3:
                return "Mount";
            case 4:
                return "Description";
            case 5:
                return "Type";
            case 6:
                return "UUID";
            case 7:
                return "Free Space";
            case 8:
                return "Usable Space";
            case 9:
                return "Total Space";
            case 10:
                return "Free Inodes";
            case 11:
                return "Total Inodes";
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return fileStores[rowIndex].getName();
            case 1:
                return fileStores[rowIndex].getVolume();
            case 2:
                return fileStores[rowIndex].getLogicalVolume();
            case 3:
                return fileStores[rowIndex].getMount();
            case 4:
                return fileStores[rowIndex].getDescription();
            case 5:
                return fileStores[rowIndex].getType();
            case 6:
                return fileStores[rowIndex].getUUID();
            case 7:
                return FormatUtil.formatBytesDecimal(fileStores[rowIndex].getFreeSpace());
            case 8:
                return FormatUtil.formatBytesDecimal(fileStores[rowIndex].getUsableSpace());
            case 9:
                return FormatUtil.formatBytesDecimal(fileStores[rowIndex].getTotalSpace());
            case 10:
                return fileStores[rowIndex].getFreeInodes();
            case 11:
                return fileStores[rowIndex].getTotalInodes();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // TODO Auto-generated method stub
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        // TODO Auto-generated method stub
    }
}


class DiskTableModel implements TableModel {

    private HWDiskStore[] diskStores;

    public DiskTableModel(HWDiskStore[] diskStores) {
        this.diskStores = diskStores;
    }

    @Override
    public int getRowCount() {
        int count = 0;
        for (int i = 0; i < diskStores.length; i++) {
            count += diskStores[0].getPartitions().length;
        }
        return count;
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Identification";
            case 1:
                return "Name";
            case 2:
                return "Type";
            case 3:
                return "UUID";
            case 4:
                return "Size";
            case 5:
                return "Major";
            case 6:
                return "Minor";
            case 7:
                return "MountPoint";
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int rowCount = 0;
        for (HWDiskStore hwDiskStore : diskStores) {
            HWPartition[] partitions = hwDiskStore.getPartitions();
            for (int j = 0; j < partitions.length; j++) {
                if (rowCount == rowIndex) {
                    switch (columnIndex) {
                        case 0:
                            return partitions[j].getIdentification();
                        case 1:
                            return partitions[j].getName();
                        case 2:
                            return partitions[j].getType();
                        case 3:
                            return partitions[j].getUuid();
                        case 4:
                            return FormatUtil.formatBytesDecimal(partitions[j].getSize());
                        case 5:
                            return partitions[j].getMajor();
                        case 6:
                            return partitions[j].getMinor();
                        case 7:
                            return partitions[j].getMountPoint();
                        default:
                            return "";
                    }
                }
                rowCount++;
            }
        }
        return "";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        // TODO Auto-generated method stub
    }
}


