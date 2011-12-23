#include "cxreader.h"
#include "ui_cxreader.h"

CxReader::CxReader(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::CxReader)
{
    ui->setupUi(this);
}

CxReader::~CxReader()
{
    delete ui;
}
