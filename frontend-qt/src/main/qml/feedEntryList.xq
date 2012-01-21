declare namespace a = "http://www.w3.org/2005/Atom";
declare namespace b = "http://beefochu.dyndns.org/atom-cxReader-extensions";

<entries> {
  for $x in doc($file)/a:feed/a:entry
  return <entry title="{$x/a:title/text()}" summary="{$x/a:summary/text()}" id="{$x/b:cxReader/b:id}" />
} </entries>
