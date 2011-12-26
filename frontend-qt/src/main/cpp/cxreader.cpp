#include "cxreader.h"
#include "ui_cxreader.h"

CxReader::CxReader(QString baseUrl, QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::CxReader)
{
    ui->setupUi(this);

    this->feedListModel = new FeedListModel(baseUrl, this);
    this->ui->feedList->setModel(this->feedListModel);
}

CxReader::~CxReader()
{
    delete feedListModel;
    delete ui;
}
