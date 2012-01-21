#include "cxreader.h"
#include "ui_cxreader.h"

CxReader::CxReader(Config * config, QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::CxReader), config(config), feedEntryListModel(NULL)
{
    ui->setupUi(this);

    this->feedListModel = new FeedListModel(config, this);
    this->ui->feedList->setModel(this->feedListModel);
    this->ui->feedList->setSelectionMode(QAbstractItemView::SingleSelection);
    connect(this->ui->feedList->selectionModel(), SIGNAL(currentChanged(QModelIndex,QModelIndex)), SLOT(feedChanged(QModelIndex,QModelIndex)));


}

CxReader::~CxReader()
{
    if(feedEntryListModel != NULL)
        delete feedEntryListModel;
    delete feedListModel;
    delete ui;
}

void CxReader::feedChanged(QModelIndex current, QModelIndex previous)
{
    FeedEntryListModel * old = feedEntryListModel;

    QString id = this->feedListModel->data(current, FeedListModel::ID).toString();
    this->feedEntryListModel = new FeedEntryListModel(this->config, id, this);
    this->ui->feedEntryList->setModel(this->feedEntryListModel);

    if(old != NULL)
        delete old;
}
