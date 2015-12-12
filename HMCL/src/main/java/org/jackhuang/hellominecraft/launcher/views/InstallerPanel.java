/*
 * Hello Minecraft! Launcher.
 * Copyright (C) 2013  huangyuhui <huanghongxun2008@126.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see {http://www.gnu.org/licenses/}.
 */
package org.jackhuang.hellominecraft.launcher.views;

import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.jackhuang.hellominecraft.C;
import org.jackhuang.hellominecraft.launcher.settings.Settings;
import org.jackhuang.hellominecraft.launcher.utils.installers.InstallerType;
import org.jackhuang.hellominecraft.launcher.utils.installers.InstallerVersionList;
import org.jackhuang.hellominecraft.tasks.TaskRunnable;
import org.jackhuang.hellominecraft.tasks.TaskRunnableArg1;
import org.jackhuang.hellominecraft.tasks.TaskWindow;
import org.jackhuang.hellominecraft.tasks.communication.DefaultPreviousResult;
import org.jackhuang.hellominecraft.utils.MessageBox;
import org.jackhuang.hellominecraft.utils.StrUtils;
import org.jackhuang.hellominecraft.utils.SwingUtils;

/**
 *
 * @author huangyuhui
 */
public class InstallerPanel extends AnimatedPanel {

    GameSettingsPanel gsp;

    /**
     * Creates new form InstallerPanel
     *
     * @param gsp           To get the minecraft version
     * @param installerType load which installer
     */
    public InstallerPanel(GameSettingsPanel gsp, InstallerType installerType) {
        initComponents();

        setOpaque(false);
        this.gsp = gsp;
        id = installerType;
        list = Settings.getInstance().getDownloadSource().getProvider().getInstallerByType(id);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnInstall = new javax.swing.JButton();
        jScrollPane12 = new javax.swing.JScrollPane();
        lstInstallers = new javax.swing.JTable();
        btnRefresh = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jackhuang/hellominecraft/launcher/I18N"); // NOI18N
        btnInstall.setText(bundle.getString("ui.button.install")); // NOI18N
        btnInstall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInstallActionPerformed(evt);
            }
        });

        lstInstallers.setModel(SwingUtils.makeDefaultTableModel(new String[]{C.I18N.getString("install.version"), C.I18N.getString("install.mcversion")},
            new Class[]{String.class, String.class}, new boolean[]{false, false}));
    lstInstallers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    jScrollPane12.setViewportView(lstInstallers);

    btnRefresh.setText(bundle.getString("ui.button.refresh")); // NOI18N
    btnRefresh.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnRefreshActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(btnInstall, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(btnRefresh, javax.swing.GroupLayout.Alignment.TRAILING)))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addComponent(btnInstall)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btnRefresh)
            .addGap(0, 0, Short.MAX_VALUE))
    );
    }// </editor-fold>//GEN-END:initComponents

    private void btnInstallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInstallActionPerformed
        downloadSelectedRow();
    }//GEN-LAST:event_btnInstallActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        refreshVersions();
    }//GEN-LAST:event_btnRefreshActionPerformed

    List<InstallerVersionList.InstallerVersion> versions;
    InstallerVersionList list;
    InstallerType id;

    void refreshVersions() {
        if (TaskWindow.getInstance().addTask(new TaskRunnableArg1<>(C.i18n("install." + id.id + ".get_list"), list)
            .registerPreviousResult(new DefaultPreviousResult<>(new String[] {gsp.getMinecraftVersionFormatted()})))
            .start())
            loadVersions();
    }

    public InstallerVersionList.InstallerVersion getVersion(int idx) {
        return versions.get(idx);
    }

    void downloadSelectedRow() {
        int idx = lstInstallers.getSelectedRow();
        if (versions == null || idx < 0 || idx >= versions.size()) {
            MessageBox.Show(C.i18n("install.not_refreshed"));
            return;
        }
        gsp.getProfile().getInstallerService().download(getVersion(idx), id).after(new TaskRunnable(this::refreshVersions)).run();
    }

    public void loadVersions() {
        SwingUtilities.invokeLater(() -> {
            synchronized (InstallerPanel.this) {
                DefaultTableModel model = (DefaultTableModel) lstInstallers.getModel();
                String mcver = StrUtils.formatVersion(gsp.getMinecraftVersionFormatted());
                versions = list.getVersions(mcver);
                SwingUtils.clearDefaultTable(lstInstallers);
                if (versions != null)
                    for (InstallerVersionList.InstallerVersion v : versions)
                        model.addRow(new Object[] {v.selfVersion == null ? "null" : v.selfVersion, v.mcVersion == null ? "null" : v.mcVersion});
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInstall;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JTable lstInstallers;
    // End of variables declaration//GEN-END:variables
}
