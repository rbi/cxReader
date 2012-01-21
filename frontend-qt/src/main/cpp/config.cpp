#include "config.h"
#include "ui_config.h"

Config::Config(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::Config)
{
    ui->setupUi(this);

    this->_manager = new QNetworkAccessManager();
    connect(this->_manager, SIGNAL(authenticationRequired(QNetworkReply*,QAuthenticator*)), SLOT(authenticate(QNetworkReply*,QAuthenticator*)));
    //TODO network errors
}

Config::~Config()
{
    delete ui;
    delete this->_manager;
}


QNetworkAccessManager * Config::manager()
{
    return this->_manager;
}

QString Config::baseUrl()
{
    QString baseUrl = this->ui->baseUrl->text();
    if(baseUrl.endsWith("/"))
        return baseUrl.left(baseUrl.size()-1);
    return baseUrl;
}


void Config::authenticate(QNetworkReply* reply, QAuthenticator* authenticator)
{
    authenticator->setUser(this->ui->userName->text());
    authenticator->setPassword(this->ui->password->text());
}
