/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.App;
import cn.devkits.client.DKConstants;
import cn.devkits.client.service.impl.TodoTaskServiceImpl;
import cn.devkits.client.tray.model.TodoTaskModel;
import cn.devkits.client.util.DKSystemUIUtil;
import cn.devkits.client.util.SpringUtilities;
import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import com.google.common.eventbus.Subscribe;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import static com.cronutils.model.CronType.SPRING;


/**
 * <p>
 * 创建待办任务对话框
 * </p>
 *
 * @author liushaofeng
 * @since 2020/8/6
 */
public class NewTodoTaskFrame extends DKAbstractFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewTodoTaskFrame.class);
    private static final int JTextField_COLUMN_20 = 20;
    private TodoListFrame todoListFrame;
    private JTextField nameTextField;
    private JTextField cornTextField;
    private JTextArea desTextArea;
    private JTextField emailsInput;
    private boolean isEmailReminder = false;

    public NewTodoTaskFrame(TodoListFrame todoListFrame) {
        super(DKSystemUIUtil.getLocaleString("TODO_NEW_DIALOG_TITLE"), 0.6f);

        initUI(getContentPane());
        initListener();

        this.todoListFrame = todoListFrame;
        App.getEventBus().register(this);
    }

    /**
     * initUI
     *
     * @param rootContainer Root Pane
     */
    @Override
    protected void initUI(Container rootContainer) {
        JPanel panel = new JPanel(new SpringLayout());

        JLabel nameLbl = new JLabel(DKSystemUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_NAME"), JLabel.TRAILING);
        panel.add(nameLbl);
        this.nameTextField = new JTextField();
        nameLbl.setLabelFor(nameTextField);
        panel.add(nameTextField);

        JLabel reminderLbl = new JLabel(DKSystemUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_REMINDER"), JLabel.TRAILING);
        panel.add(reminderLbl);
        JPanel reminderPane = createReminderPane();
        reminderLbl.setLabelFor(reminderPane);
        panel.add(reminderPane);

        JLabel cornLbl = new JLabel(DKSystemUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_CORN"), JLabel.TRAILING);
        panel.add(cornLbl);
        JPanel cornTextFieldPane = createCornPane();
        cornLbl.setLabelFor(cornTextFieldPane);
        panel.add(cornTextFieldPane);

        JLabel descLbl = new JLabel(DKSystemUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_DESC"), JLabel.TRAILING);
        panel.add(descLbl);
        JScrollPane descTextAreaPane = createDescTextArea();
        descLbl.setLabelFor(descTextAreaPane);
        panel.add(descTextAreaPane);

        SpringUtilities.makeCompactGrid(panel,
                4, 2,
                DKSystemUIUtil.COMPONENT_UI_PADDING_5, DKSystemUIUtil.COMPONENT_UI_PADDING_5,
                6, 6);

        rootContainer.add(panel, BorderLayout.CENTER);
        rootContainer.add(createBottomPane(), BorderLayout.PAGE_END);
    }

    private JPanel createReminderPane() {
        JPanel jPanel = new JPanel(new SpringLayout());
        JRadioButton reminderTypeOfTrayMsg = new JRadioButton(DKSystemUIUtil.getLocaleString("TODO_NEW_DIALOG_REMINDER_TRAY_MSG"));
        reminderTypeOfTrayMsg.setSelected(true);
        jPanel.add(reminderTypeOfTrayMsg);

        JRadioButton reminderTypeOfEmail = new JRadioButton(DKSystemUIUtil.getLocaleString("TODO_NEW_DIALOG_REMINDER_EMAIL"));
        jPanel.add(reminderTypeOfEmail);

        ButtonGroup group = new ButtonGroup();
        group.add(reminderTypeOfTrayMsg);
        group.add(reminderTypeOfEmail);

        this.emailsInput = new JTextField();
        emailsInput.setToolTipText(DKSystemUIUtil.getLocaleString("TODO_NEW_DIALOG_REMINDER_EMAIL_TOOLTIPS"));
        emailsInput.setEnabled(false);
        jPanel.add(emailsInput);

        reminderTypeOfEmail.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JRadioButton item = (JRadioButton) e.getItem();
                emailsInput.setEnabled(item.isSelected());
                isEmailReminder = item.isSelected();
            }
        });

        SpringUtilities.makeCompactGrid(jPanel,
                1, 3,
                DKSystemUIUtil.COMPONENT_UI_PADDING_0, DKSystemUIUtil.COMPONENT_UI_PADDING_5,
                DKSystemUIUtil.COMPONENT_UI_PADDING_0, 2);

        return jPanel;
    }

    private JPanel createCornPane() {
        JPanel jPanel = new JPanel(new SpringLayout());

        this.cornTextField = new JTextField();
        jPanel.add(cornTextField);
        JLabel cornHelpIconLbl = createCornHelpIcon();
        jPanel.add(cornHelpIconLbl);

        SpringUtilities.makeCompactGrid(jPanel,
                1, 2,
                DKSystemUIUtil.COMPONENT_UI_PADDING_0, DKSystemUIUtil.COMPONENT_UI_PADDING_5,
                2, 2);
        return jPanel;
    }

    private JLabel createCornHelpIcon() {
        Icon icon = IconFontSwing.buildIcon(FontAwesome.QUESTION, 16, new Color(50, 50, 50));
        JLabel helpIconLbl = new JLabel();
        helpIconLbl.setIcon(icon);
        helpIconLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String cornUrl = "https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm";
                try {
                    DKSystemUIUtil.browseURL(new URI(cornUrl));
                } catch (URISyntaxException uriSyntaxException) {
                    LOGGER.error("Can't convert string to URI: {}", cornUrl);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        return helpIconLbl;
    }

    private JScrollPane createDescTextArea() {
        JScrollPane jScrollPane = new JScrollPane();

        this.desTextArea = new JTextArea();
        desTextArea.setRows(10);
        desTextArea.setColumns(40);
        jScrollPane.setViewportView(desTextArea);

        return jScrollPane;
    }

    private JPanel createBottomPane() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        buttonPane.add(Box.createHorizontalGlue());

        JButton applyBtn = new JButton(DKSystemUIUtil.getLocaleString("COMMON_BTNS_CREATE"));
        applyBtn.addActionListener(e -> {
            TodoTaskServiceImpl service = (TodoTaskServiceImpl) App.getContext().getBean("todoTaskServiceImpl");
            //TODO input check
            TodoTaskModel todoTaskModel = new TodoTaskModel(nameTextField.getText(), cornTextField.getText(), desTextArea.getText());
            todoTaskModel.setReminder(isEmailReminder ? DKConstants.TODO_REMINDER.EMAIL : DKConstants.TODO_REMINDER.TRAY);
            todoTaskModel.setEmail(emailsInput.getText());

            service.newTodoTask(todoTaskModel);
            App.getEventBus().post(todoTaskModel);
            this.setVisible(false);

            // 快捷键方式创建待办不用刷新
            if (todoListFrame != null) {
                todoListFrame.refresh();
            }
        });

        buttonPane.add(applyBtn);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton closeBtn = new JButton(DKSystemUIUtil.getLocaleString("COMMON_BTNS_CLOSE"));
        closeBtn.addActionListener(e -> {
            this.setVisible(false);
        });
        buttonPane.add(closeBtn);

        return buttonPane;
    }

    @Override
    protected void initListener() {
        cornTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTooltips();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTooltips();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTooltips();
            }

            private void updateTooltips() {
                CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(SPRING);
                CronParser parser = new CronParser(cronDefinition);
                CronDescriptor descriptor = CronDescriptor.instance(Locale.CHINA);
                try {
                    Cron parse = parser.parse(cornTextField.getText().trim());
                    String description = descriptor.describe(parse);
                    cornTextField.setToolTipText(description);
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("The input corn expression is invalid!");
                }
            }
        });
    }

    @Override
    public boolean isResizable() {
        return false;
    }


}
