import { WindowRef , setApz, apz} from "./appzillon.service";
describe('AppzillonService WindowRef', () => {
    let appzillonService: WindowRef;
    beforeEach(() => { appzillonService = new WindowRef(); });
  
    it('should return window object on invoking nativeWindow()', () => {
        expect(appzillonService.nativeWindow).toEqual(window);
    });

    it('should set apz value on invoking setApz()', () => {
        setApz({'test': 'Test Value'});
        expect(apz.test).toEqual('Test Value');
    });

});