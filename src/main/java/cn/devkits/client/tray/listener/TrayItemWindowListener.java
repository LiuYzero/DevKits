package cn.devkits.client.tray.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import cn.devkits.client.tray.MenuItemEnum;
import cn.devkits.client.tray.frame.CodeFormatFrame;
import cn.devkits.client.tray.frame.LargeDuplicateFilesFrame;
import cn.devkits.client.tray.frame.ServerPortsFrame;
import cn.devkits.client.tray.window.ScreenCaptureWindow;

public class TrayItemWindowListener implements ActionListener {

    private MenuItemEnum itemEnum;

    public TrayItemWindowListener(MenuItemEnum codeFormat) {
        this.itemEnum = codeFormat;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = null;
        switch (itemEnum) {
            case CODEC:

                break;
            case SERVER_PORT:
                frame = new ServerPortsFrame();
                break;
            case CODE_FORMAT:
                frame = new CodeFormatFrame();
                break;
            case LDF:
                frame = new LargeDuplicateFilesFrame();
                break;
            case SCRCAPTURE:
                new ScreenCaptureWindow().setVisible(true);;
                return;
            default:
                break;
        }

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

}
