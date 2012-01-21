#ifndef CONFIG_H
#define CONFIG_H

#include <QDialog>
#include <QNetworkAccessManager>
#include <QAuthenticator>
#include <QNetworkReply>

namespace Ui {
    class Config;
}

class Config : public QDialog
{
    Q_OBJECT

public:
    explicit Config(QWidget *parent = 0);
    ~Config();
    QString baseUrl();
    QNetworkAccessManager * manager();

private:
    Ui::Config *ui;
    QNetworkAccessManager * _manager;

private slots:
    void authenticate(QNetworkReply* reply, QAuthenticator* authenticator);
};

#endif // CONFIG_H
