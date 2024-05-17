import { SignerWithAddress } from '@nomiclabs/hardhat-ethers/dist/src/signers';
import chai from 'chai'
import { solidity } from 'ethereum-waffle'
import { BigNumber } from 'ethers';
//import { chaiAsPromised } from 'chai-as-promised'
import { ethers } from 'hardhat'
import { IndexerToken, IndexerToken__factory  } from '../../typechain-types'
import * as c from '../consts'

chai.use(solidity)
//chai.use(chaiAsPromised)
const { expect } = chai

describe('IndexerToken', () => {
  let token: IndexerToken
  let owner: SignerWithAddress
  let user1: SignerWithAddress
  let user2: SignerWithAddress

  before(async () => {
    ;[owner, user1, user2] = await ethers.getSigners()

    const factory = new IndexerToken__factory(owner)
    token = await factory.deploy(c.T_1000)
    await token.deployed()
    expect(await token.totalSupply()).to.eq(c.T_1000)
  })

  it('create_and_transfer', async () => {
    expect(await token.balanceOf(owner.address)).to.eq(c.T_1000)

    let tx = await token.connect(owner).transfer(user1.address, c.T_100)
    await tx.wait()

    expect(await token.balanceOf(owner.address)).to.eq(BigNumber.from(c.T_1000).sub(BigNumber.from(c.T_100)))
    expect(await token.balanceOf(user1.address)).to.eq(c.T_100)

    tx = await token.connect(user1).transfer(user2.address, c.T_50)
    await tx.wait()

    expect(await token.balanceOf(user1.address)).to.eq(c.T_50)
    expect(await token.balanceOf(user2.address)).to.eq(c.T_50)
  })
})