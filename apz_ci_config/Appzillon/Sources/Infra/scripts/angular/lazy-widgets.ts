import { Type } from '@angular/core';

export const lazyWidgets: { name: string, loadChildren: () => Promise<Type<any>> }[] = []


APZLAZYWIDGET

export function lazyArrayToObj() {
  const result:any = {};
  for (const w of lazyWidgets) {
    result[w.name] = w.loadChildren;
  }
  return result;
}
